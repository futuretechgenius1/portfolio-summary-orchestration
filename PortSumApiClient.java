package com.fmr.portfolio.client;

import com.fmr.portfolio.dto.CustomerContractRequest;
import com.fmr.portfolio.dto.PortfolioSummaryResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PortSumApiClient {

    private final RestTemplate aifRestTemplate;
    private final RestTemplate ociRestTemplate;

    public PortSumApiClient(
            @Qualifier("aifRestTemplate") RestTemplate aifRestTemplate,
            @Qualifier("ociRestTemplate") RestTemplate ociRestTemplate) {
        this.aifRestTemplate = aifRestTemplate;
        this.ociRestTemplate = ociRestTemplate;
    }

    public PortfolioSummaryResponse callAif(CustomerContractRequest request) {
        return call(request, aifRestTemplate, "AIF");
    }

    public PortfolioSummaryResponse callOci(CustomerContractRequest request) {
        return call(request, ociRestTemplate, "OCI");
    }

    private PortfolioSummaryResponse call(CustomerContractRequest request, RestTemplate template, String system) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerContractRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<PortfolioSummaryResponse> response = template.exchange(
                    "/tgp/id/lifeInsurance/customerContractSummary/v1",
                    HttpMethod.POST,
                    entity,
                    PortfolioSummaryResponse.class
            );
            PortfolioSummaryResponse body = response.getBody();
            if (body != null && body.getContracts() != null) {
                body.getContracts().forEach(c -> c.setSysOfRcrd(system));
            }
            return body != null ? body : PortfolioSummaryResponse.builder().build();
        } catch (Exception e) {
            System.err.println("Error calling " + system + ": " + e.getMessage());
            return PortfolioSummaryResponse.builder().build();
        }
    }
}
