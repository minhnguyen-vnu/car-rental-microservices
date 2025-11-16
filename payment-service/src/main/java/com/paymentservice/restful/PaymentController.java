package com.paymentservice.restful;

import com.paymentservice.core.dto.PaymentDTO;
import com.paymentservice.core.dto.request.PaymentChargeRequestDTO;
import com.paymentservice.core.dto.response.PaymentChargeResponseDTO;
import com.paymentservice.core.response.GeneralResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/payments")
public interface PaymentController {

    @PostMapping("/charge")
    GeneralResponse<PaymentChargeResponseDTO> charge(@RequestBody PaymentChargeRequestDTO request);

    @GetMapping("/{paymentCode}")
    GeneralResponse<PaymentDTO> getByCode(@PathVariable("paymentCode") String paymentCode);

}
