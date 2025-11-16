package com.rentalservice.ui.restful.impl;

import com.rentalservice.core.constant.response.GeneralResponse;
import com.rentalservice.core.dto.request.RentalCreateRequestDTO;
import com.rentalservice.core.dto.request.RentalRequestDTO;
import com.rentalservice.core.dto.response.RentalResponseDTO;
import com.rentalservice.core.service.RentalService;
import com.rentalservice.kernel.mapper.RentalMapper;
import com.rentalservice.ui.restful.RentalController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RentalControllerImpl implements RentalController {
    private final RentalService rentalService; 

    @Override
    public GeneralResponse<RentalResponseDTO> createRental(RentalCreateRequestDTO request) {
        return GeneralResponse.ok(RentalMapper.toResponse(rentalService.createRental(request)));
    }

    @Override
    public GeneralResponse<RentalResponseDTO> updateRental(RentalRequestDTO request) {
        return GeneralResponse.ok(RentalMapper.toResponse(rentalService.updateRental(request)));
    }

    @Override
    public GeneralResponse<List<RentalResponseDTO>> getRental(RentalRequestDTO request) {
        return GeneralResponse.ok(RentalMapper.toResponseList(rentalService.getRentals(request)));
    }

}
