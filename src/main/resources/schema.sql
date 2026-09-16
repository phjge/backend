CREATE DATABASE IF NOT EXISTS news_credibility;

USE news_credibility;

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 분석 캐시 테이블 (동일 텍스트 재분석 방지)
CREATE TABLE IF NOT EXISTS analysis_cache (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text_hash VARCHAR(255) NOT NULL UNIQUE,
    exaggeration FLOAT,
    ai_prob FLOAT,
    label VARCHAR(50),
    credibility FLOAT,
    press_name VARCHAR(100),
    final_score FLOAT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 분석 이력 테이블 (모든 분석 기록 누적)
CREATE TABLE IF NOT EXISTS analysis_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    text_hash VARCHAR(255),
    final_score FLOAT,
    label VARCHAR(50),
    press_name VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 언론사 화이트리스트 테이블
CREATE TABLE IF NOT EXISTS press_whitelist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    domain VARCHAR(255) NOT NULL UNIQUE,
    press_name VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);