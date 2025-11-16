package com.paymentservice.restful;

import com.paymentservice.core.dto.request.VnpIpnRequestDTO;
import com.paymentservice.core.dto.request.VnpReturnRequestDTO;
import com.paymentservice.core.dto.response.VnpReturnViewDTO;
import com.paymentservice.core.response.GeneralResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public interface VnPayController {
    @GetMapping("/vnpay/return")
    GeneralResponse<Void> vnpReturn(@ModelAttribute VnpReturnRequestDTO req);
}
