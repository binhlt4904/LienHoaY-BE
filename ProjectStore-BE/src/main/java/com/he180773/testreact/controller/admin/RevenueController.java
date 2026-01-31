package com.he180773.testreact.controller.admin;

import com.he180773.testreact.dto.RevenueDTO;
import com.he180773.testreact.service.RevenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/revenue")
//@CrossOrigin(origins = "https://leventstreetwear.id.vn")
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getRevenue(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day
    ) {
        List<RevenueDTO> dailyRevenue = revenueService.getRevenueFiltered(year, month, day);
        long totalRevenue = dailyRevenue.stream().mapToLong(RevenueDTO::getTotalRevenue).sum();

        Map<String, Object> res = new HashMap<>();
        res.put("dailyRevenue", dailyRevenue);
        res.put("totalRevenue", totalRevenue);
        return ResponseEntity.ok(res);
    }

}

