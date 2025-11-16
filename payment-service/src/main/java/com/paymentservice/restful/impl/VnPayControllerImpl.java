package com.paymentservice.restful.impl;

import com.paymentservice.core.dto.request.VnpIpnRequestDTO;
import com.paymentservice.core.dto.request.VnpReturnRequestDTO;
import com.paymentservice.core.dto.response.VnpReturnViewDTO;
import com.paymentservice.core.response.GeneralResponse;
import com.paymentservice.core.service.PaymentService;
import com.paymentservice.restful.VnPayController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VnPayControllerImpl implements VnPayController {
    private final PaymentService paymentService;

    @Override
    public GeneralResponse<Void> vnpReturn(VnpReturnRequestDTO req) {
        paymentService.handleVnpReturn(req);
        return GeneralResponse.ok(null);
    }
}
