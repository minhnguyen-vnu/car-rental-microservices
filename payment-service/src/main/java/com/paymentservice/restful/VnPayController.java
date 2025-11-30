package com.paymentservice.restful;

import com.paymentservice.core.dto.request.VnpIpnRequestDTO;
import com.paymentservice.core.dto.request.VnpReturnRequestDTO;
import com.paymentservice.core.dto.response.VnpReturnViewDTO;
import com.paymentservice.core.response.GeneralResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

public interface VnPayController {
    @GetMapping("/api/vnpay/return")
    String vnpReturn(@ModelAttribute VnpReturnRequestDTO req);
}
