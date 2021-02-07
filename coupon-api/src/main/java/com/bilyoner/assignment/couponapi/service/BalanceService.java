package com.bilyoner.assignment.couponapi.service;

import com.bilyoner.assignment.couponapi.model.UpdateBalanceRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BalanceService {

    @Value("${webservice.balanceApi.endpoint}")
    private String balanceApiEndpoint;

    public void updateBalance(UpdateBalanceRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(balanceApiEndpoint, request);
    }
}
