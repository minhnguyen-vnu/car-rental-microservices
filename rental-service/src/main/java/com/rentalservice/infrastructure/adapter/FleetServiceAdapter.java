package com.rentalservice.infrastructure.adapter;

import com.rentalservice.core.constant.RemoteUrls;
import com.rentalservice.core.constant.response.GeneralResponse;
import com.rentalservice.core.context.LocalContextHolder;
import com.rentalservice.core.dto.request.VehicleBlockRequestDTO;
import com.rentalservice.core.dto.request.VehicleRequestDTO;
import com.rentalservice.core.dto.response.VehicleBlockResponseDTO;
import com.rentalservice.core.dto.response.VehicleResponseDTO;
import com.rentalservice.kernel.client.RestClientTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FleetServiceAdapter {

    @Value("${client.fleet.service}")
    private String fleetServiceUrl;

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(LocalContextHolder.get().getToken());
        return headers;
    }

    public List<VehicleResponseDTO> getVehicleDetails(VehicleRequestDTO request) {
        RestClientTemplate restClientTemplate = RestClientTemplate.withTimeouts(Duration.ofSeconds(5), Duration.ofSeconds(5));
        String url = fleetServiceUrl.concat(RemoteUrls.Fleet.GET_VEHICLE_DETAILS);
        return restClientTemplate.postForList(url, request, headers(), new ParameterizedTypeReference<GeneralResponse<List<VehicleResponseDTO>>>() {});
    }

    public List<VehicleBlockResponseDTO> getVehicleBlocks(VehicleBlockRequestDTO request) {
        RestClientTemplate restClientTemplate = RestClientTemplate.withTimeouts(Duration.ofSeconds(5), Duration.ofSeconds(5));
        String url = fleetServiceUrl.concat(RemoteUrls.Fleet.GET_VEHICLE_BLOCKS);
        return restClientTemplate.postForList(url, request, headers(), new ParameterizedTypeReference<GeneralResponse<List<VehicleBlockResponseDTO>>>() {});
    }
}
