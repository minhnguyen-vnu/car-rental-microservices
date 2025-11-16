package com.paymentservice.core.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VnpIpnRequestDTO {
    private String vnp_TmnCode;
    private String vnp_Amount;
    private String vnp_BankCode;
    private String vnp_OrderInfo;
    private String vnp_ResponseCode;
    private String vnp_TransactionNo;
    private String vnp_TxnRef;
    private String vnp_PayDate;
    private String vnp_SecureHashType;
    private String vnp_SecureHash;
}
