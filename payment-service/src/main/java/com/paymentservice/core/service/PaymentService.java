package com.paymentservice.core.service;

import com.paymentservice.core.dto.PaymentDTO;
import com.paymentservice.core.dto.request.PaymentChargeRequestDTO;
import com.paymentservice.core.dto.request.VnpIpnRequestDTO;
import com.paymentservice.core.dto.request.VnpReturnRequestDTO;
import com.paymentservice.core.dto.response.PaymentChargeResponseDTO;
import com.paymentservice.core.dto.response.VnpReturnViewDTO;

public interface PaymentService {
    PaymentChargeResponseDTO charge(PaymentChargeRequestDTO req);
    PaymentDTO getByCode(String paymentCode);

    String handleVnpReturn(VnpReturnRequestDTO req);
    void sync(); 

}
