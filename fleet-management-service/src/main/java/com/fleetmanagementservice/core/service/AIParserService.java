package com.fleetmanagementservice.core.service;

import com.fleetmanagementservice.core.dto.request.VehicleRequestDTO;

public interface AIParserService {
    VehicleRequestDTO parseVehicleQuery(String freeText);
    String buildPrompt(String freeText);
    String callAI(String prompt);
    VehicleRequestDTO parseResponse(String jsonText);
    boolean validateCredentials();
    String getProviderName();
}
