package com.fmr.portfolio.service;

import com.fmr.portfolio.client.PortSumApiClient;
import com.fmr.portfolio.dto.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PortfolioSummaryOrchestrationService {

    private final PortSumApiClient client;

    public PortfolioSummaryOrchestrationService(PortSumApiClient client) {
        this.client = client;
    }

    public PortfolioSummaryResponse getContracts(CustomerContractRequest request) {
        CompletableFuture<PortfolioSummaryResponse> aif = callAifAsync(request);
        CompletableFuture<PortfolioSummaryResponse> oci = callOciAsync(request);

        CompletableFuture.allOf(aif, oci).join();

        List<Contract> all = new ArrayList<>();
        addAll(aif.join(), all);
        addAll(oci.join(), all);

        all.sort((c1, c2) -> 
            Double.compare(c2.getContractVal() != null ? c2.getContractVal() : 0,
                           c1.getContractVal() != null ? c1.getContractVal() : 0));

        return PortfolioSummaryResponse.builder()
                .customerID("25001007")
                .customerDetails(CustomerDetails.builder().customerID("25001007").build())
                .contracts(all)
                .build();
    }

    @Async
    public CompletableFuture<PortfolioSummaryResponse> callAifAsync(CustomerContractRequest r) {
        return CompletableFuture.completedFuture(client.callAif(r));
    }

    @Async
    public CompletableFuture<PortfolioSummaryResponse> callOciAsync(CustomerContractRequest r) {
        return CompletableFuture.completedFuture(client.callOci(r));
    }

    private void addAll(PortfolioSummaryResponse resp, List<Contract> target) {
        if (resp != null && resp.getContracts() != null) {
            target.addAll(resp.getContracts());
        }
    }
}
