package com.paymentservice.restful.impl;

import com.paymentservice.core.dto.request.VnpIpnRequestDTO;
import com.paymentservice.core.dto.request.VnpReturnRequestDTO;
import com.paymentservice.core.dto.response.VnpReturnViewDTO;
import com.paymentservice.core.response.GeneralResponse;
import com.paymentservice.core.service.PaymentService;
import com.paymentservice.restful.VnPayController;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class VnPayControllerImpl implements VnPayController {
    private final PaymentService paymentService;

    @Override
    public String vnpReturn(VnpReturnRequestDTO req) {
        return paymentService.handleVnpReturn(req);
    }
}
