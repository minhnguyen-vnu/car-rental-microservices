package com.rentalservice.ui.restful;

import com.rentalservice.core.constant.response.GeneralResponse;
import com.rentalservice.core.dto.request.RentalCreateRequestDTO;
import com.rentalservice.core.dto.request.RentalRequestDTO;
import com.rentalservice.core.dto.response.RentalResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/rental")
public interface RentalController {
    @PostMapping("/create")
    public GeneralResponse<RentalResponseDTO> createRental(@RequestBody RentalCreateRequestDTO request);

    @PutMapping("/update")
    public GeneralResponse<RentalResponseDTO> updateRental(@RequestBody RentalRequestDTO request);

    @PostMapping("/get")
    public GeneralResponse<List<RentalResponseDTO>> getRental(@RequestBody RentalRequestDTO request);
}
