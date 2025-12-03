package com.fleetmanagementservice.core.service;

import com.fleetmanagementservice.core.document.VehicleDocument;

import java.util.List;

public interface VehicleSearchService {
    void reindex();
    List<VehicleDocument> searchByFreeText(String query);
}
