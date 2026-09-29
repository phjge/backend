package com.newscredibility.backend.controller;

import com.newscredibility.backend.dto.HistoryItemDTO;
import com.newscredibility.backend.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    /** GET /api/history?page=0&size=10 */
    @GetMapping
    public ResponseEntity<Page<HistoryItemDTO>> getMyHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(historyService.getMyHistory(page, size));
    }

    /** DELETE /api/history/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        historyService.deleteMyHistory(id);
        return ResponseEntity.noContent().build();
    }
}
