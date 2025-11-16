package com.paymentservice.restful.impl;

import com.paymentservice.core.dto.PaymentDTO;
import com.paymentservice.core.dto.request.PaymentChargeRequestDTO;
import com.paymentservice.core.dto.response.PaymentChargeResponseDTO;
import com.paymentservice.core.response.GeneralResponse;
import com.paymentservice.core.service.PaymentService;
import com.paymentservice.restful.PaymentController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PaymentControllerImpl implements PaymentController {
    private final PaymentService paymentService;


    @Override
    public GeneralResponse<PaymentChargeResponseDTO> charge(PaymentChargeRequestDTO request) {
        return GeneralResponse.ok(paymentService.charge(request));
    }

    @Override
    public GeneralResponse<PaymentDTO> getByCode(String paymentCode) {
        return GeneralResponse.ok(paymentService.getByCode(paymentCode));
    }
}
