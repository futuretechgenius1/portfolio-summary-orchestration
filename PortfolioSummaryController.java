package com.fmr.portfolio.controller;

import com.fmr.portfolio.dto.CustomerContractRequest;
import com.fmr.portfolio.dto.PortfolioSummaryResponse;
import com.fmr.portfolio.service.PortfolioSummaryOrchestrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolio")
public class PortfolioSummaryController {

    private final PortfolioSummaryOrchestrationService service;

    public PortfolioSummaryController(PortfolioSummaryOrchestrationService service) {
        this.service = service;
    }

    @PostMapping("/customerContractSummary/v1")
    public ResponseEntity<PortfolioSummaryResponse> getContracts(
            @RequestBody CustomerContractRequest request) {
        return ResponseEntity.ok(service.getContracts(request));
    }
}
