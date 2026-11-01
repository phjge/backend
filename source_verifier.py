"""
source_verifier.py
NAVER News API 기반 출처 검증 모듈
팀폴6 | AI 기반 뉴스 신뢰도 평가 플랫폼 (DSD v1.0)
"""

from __future__ import annotations

import os
import re
import logging
from typing import List

import requests
from dotenv import load_dotenv

load_dotenv()
logger = logging.getLogger(__name__)

# NAVER API 설정
_NAVER_API_URL = "https://openapi.naver.com/v1/search/news.json"
_CLIENT_ID     = os.getenv("NAVER_CLIENT_ID", "")
_CLIENT_SECRET = os.getenv("NAVER_CLIENT_SECRET", "")

#_NAVER_API_URL = "https://naverapihub.apigw.ntruss.com/search/v1/news"
#   headers = {
#     "X-NCP-APIGW-API-KEY-ID": _CLIENT_ID,
#     "X-NCP-APIGW-API-KEY":    _CLIENT_SECRET,
#   }



# 언론사 화이트리스트
TRUSTED_PRESS: set[str] = {
    "연합뉴스", "KBS", "MBC", "SBS", "YTN", "JTBC",
    "조선일보", "중앙일보", "동아일보", "한국경제", "매일경제",
    "한겨레", "경향신문", "오마이뉴스", "뉴시스", "뉴스1",
    "아이뉴스24", "전자신문", "디지털타임스", "헤럴드경제",
}

# ════════════════════════════════════════════════════════════════════
# [수정 제안 1 - 함수 교체본] 위 _parse_press_from_url()을 대체하는 코드
#
# 동작: URL의 호스트가 TRUSTED_DOMAINS의 도메인과 같거나 그 서브도메인이면
#       한글 언론사 이름("연합뉴스" 등)을 반환 → TRUSTED_PRESS와 일치하게 됨.
#       매핑에 없는 도메인은 호스트 전체를 반환(화이트리스트에 안 걸리므로 비신뢰 처리).
#
# 적용 방법: 위의 원본 _parse_press_from_url()을 지우거나 이름을 바꾸고,
#            아래 주석을 해제하면 됨. (아래 verify/calculate_credibility는 수정 불필요)
#
# def _parse_press_from_url(url: str) -> str:
#     host = re.sub(r"^https?://", "", url).split("/")[0].lower()
#     for domain, name in TRUSTED_DOMAINS.items():
#         if host == domain or host.endswith("." + domain):
#             return name
#     return host
#
# 적용 후 확인:
#   python -c "from source_verifier import _parse_press_from_url as p; \
#              print(p('https://www.yna.co.kr/view/AKR1'), p('https://news.kbs.co.kr/x'))"
#   → 연합뉴스 KBS 가 나오면 정상
# ════════════════════════════════════════════════════════════════════

def _remove_html_tags(text: str) -> str:
    return re.sub(r"<[^>]+>", "", text).strip()

def _extract_keywords(text: str, max_keywords: int = 3) -> str:
    tokens = text.split()
    keywords = [t for t in tokens if len(t) >= 2][:max_keywords]
    return " ".join(keywords) if keywords else text[:20]

# ════════════════════════════════════════════════════════════════════
# [수정 제안 2 - 선택] 검색 키워드 추출 개선
#
# 이유: 현재는 본문 앞 3단어(예: "정부는 오늘 내년도")를 검색어로 써서
#       관련 기사가 잘 안 잡힐 수 있음. 아래는 가장 단순한 개선안이고,
#       더 좋게 하려면 형태소 분석기(KoNLPy 등)로 명사만 뽑는 방법이 있음.
#       (제안 1과 독립적이므로 필요 없으면 무시해도 됨)
#
# def _extract_keywords(text: str, max_keywords: int = 3) -> str:
#     # 첫 문장(제목일 가능성이 높음)을 최대 30자까지 검색어로 사용
#     first_sentence = re.split(r"[.!?\n]", text.strip())[0].strip()
#     return first_sentence[:30] if first_sentence else text[:20]
# ════════════════════════════════════════════════════════════════════

def _parse_press_from_url(url: str) -> str:
    match = re.search(r"https?://(?:www\.)?([^/]+)", url)
    return match.group(1).split(".")[0] if match else "unknown"

# ════════════════════════════════════════════════════════════════════
# [수정 제안 1 - 함수 교체본] 위 _parse_press_from_url()을 대체하는 코드
#
# 동작: URL의 호스트가 TRUSTED_DOMAINS의 도메인과 같거나 그 서브도메인이면
#       한글 언론사 이름("연합뉴스" 등)을 반환 → TRUSTED_PRESS와 일치하게 됨.
#       매핑에 없는 도메인은 호스트 전체를 반환(화이트리스트에 안 걸리므로 비신뢰 처리).
#
# 적용 방법: 위의 원본 _parse_press_from_url()을 지우거나 이름을 바꾸고,
#            아래 주석을 해제하면 됨. (아래 verify/calculate_credibility는 수정 불필요)
#
# def _parse_press_from_url(url: str) -> str:
#     host = re.sub(r"^https?://", "", url).split("/")[0].lower()
#     for domain, name in TRUSTED_DOMAINS.items():
#         if host == domain or host.endswith("." + domain):
#             return name
#     return host
#
# 적용 후 확인:
#   python -c "from source_verifier import _parse_press_from_url as p; \
#              print(p('https://www.yna.co.kr/view/AKR1'), p('https://news.kbs.co.kr/x'))"
#   → 연합뉴스 KBS 가 나오면 정상
# ════════════════════════════════════════════════════════════════════

def fetch_naver_articles(keyword: str, display: int = 10) -> List[dict]:
    if not _CLIENT_ID or not _CLIENT_SECRET:
        logger.error("[source_verifier] NAVER API 키가 설정되지 않았습니다.")
        return []

    headers = {
        "X-Naver-Client-Id":     _CLIENT_ID,
        "X-Naver-Client-Secret": _CLIENT_SECRET,
    }
    params = {
        "query":   keyword,
        "display": display,
        "sort":    "sim",
    }

    try:
        response = requests.get(_NAVER_API_URL, headers=headers, params=params, timeout=10)
        response.raise_for_status()
        data = response.json()
    except requests.exceptions.Timeout:
        logger.error("[source_verifier] NAVER API 응답 시간 초과")
        return []
    except requests.exceptions.RequestException as e:
        logger.error(f"[source_verifier] NAVER API 호출 실패: {e}")
        return []

    articles = []
    for item in data.get("items", []):
        title = _remove_html_tags(item.get("title", ""))
        url   = item.get("originallink") or item.get("link", "")
        press = _parse_press_from_url(url)
        articles.append({
            "title":       title,
            "press":       press,
            "url":         url,
            "description": _remove_html_tags(item.get("description", "")),
            "pubDate":     item.get("pubDate", ""),
        })

    logger.info(f"[source_verifier] {len(articles)}건 수집")
    return articles

def calculate_credibility(articles: List[dict]) -> float:
    if not articles:
        return 0.0
    trusted_count = sum(
        1 for art in articles
        if any(trusted in art.get("press", "") for trusted in TRUSTED_PRESS)
    )
    credibility = trusted_count / len(articles)
    return round(credibility, 4)

def verify(text: str) -> dict:
    keyword  = _extract_keywords(text)
    articles = fetch_naver_articles(keyword)
    credibility = calculate_credibility(articles)

    trusted_articles = [
        a for a in articles
        if any(t in a.get("press", "") for t in TRUSTED_PRESS)
    ]
    press_name = trusted_articles[0]["press"] if trusted_articles else (
        articles[0]["press"] if articles else "unknown"
    )

    return {
        "credibility": credibility,
        "press_name":  press_name,
        "articles":    articles,
    }