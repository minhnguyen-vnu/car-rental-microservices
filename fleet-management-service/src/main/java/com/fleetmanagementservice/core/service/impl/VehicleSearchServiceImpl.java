package com.fleetmanagementservice.core.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.fleetmanagementservice.core.document.VehicleDocument;
import com.fleetmanagementservice.core.entity.Vehicle;
import com.fleetmanagementservice.core.service.VehicleSearchService;
import com.fleetmanagementservice.infrastructure.repository.VehicleRepository;
import com.fleetmanagementservice.infrastructure.repository.VehicleSearchRepository;
import com.fleetmanagementservice.kernel.mapper.VehicleMapper;
import com.fleetmanagementservice.kernel.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleSearchServiceImpl implements VehicleSearchService {
    private final VehicleSearchRepository searchRepository;
    private final VehicleRepository vehicleRepository;
    private final ElasticsearchOperations elasticsearchOperations;


    @Override
    public void reindex() {
        List<Vehicle> allVehicles = vehicleRepository.findAll();

        List<VehicleDocument> documents = allVehicles.stream()
                .map(VehicleMapper::toDocument)
                .toList();

        searchRepository.saveAll(documents);
    }

    @Override
    public List<VehicleDocument> searchByFreeText(String query) {
        if (DataUtils.isBlank(query)) {
            return Collections.emptyList();
        }

        String searchTerm = query.trim();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .query(searchTerm)
                                .fields("brand", "model", "vehicleType")
                                .type(TextQueryType.BestFields)
                        )
                )
                .build();

        SearchHits<VehicleDocument> searchHits = elasticsearchOperations.search(
                searchQuery,
                VehicleDocument.class
        );

        return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
