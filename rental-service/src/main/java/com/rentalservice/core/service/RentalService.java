package com.rentalservice.core.service;

import com.rentalservice.core.dto.request.RentalCreateRequestDTO;
import com.rentalservice.core.dto.request.RentalRequestDTO;
import com.rentalservice.core.entity.Rental;

import java.util.List;

public interface RentalService {
    public Rental createRental(RentalCreateRequestDTO request);
    public Rental updateRental(RentalRequestDTO request);
    public List<Rental> getRentals(RentalRequestDTO request);;
    public void sync();
}
