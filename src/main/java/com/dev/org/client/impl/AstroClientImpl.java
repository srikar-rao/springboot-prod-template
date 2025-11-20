package com.dev.org.client.impl;

import com.dev.org.client.AstroClient;
import com.dev.org.common.response.astro.AstronautsResponse;
import java.util.Collections;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class AstroClientImpl extends AbstractServiceClient implements AstroClient {

    private final RestClient restClient;

    public AstroClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    protected String getServiceName() {
        return "mock-api-client";
    }

    @Override
    public AstronautsResponse getAstronauts() {
        return executeRequest(
                () ->
                        restClient
                                .get()
                                .uri("/astros.json")
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .body(AstronautsResponse.class),
                Collections.emptyMap());
    }
}
