package com.paymentservice.core.service.impl;

import com.paymentservice.core.config.VnPayConfig;
import com.paymentservice.core.dto.PaymentDTO;
import com.paymentservice.core.dto.request.PaymentChargeRequestDTO;
import com.paymentservice.core.dto.request.VnpIpnRequestDTO;
import com.paymentservice.core.dto.request.VnpReturnRequestDTO;
import com.paymentservice.core.dto.response.PaymentChargeResponseDTO;
import com.paymentservice.core.dto.response.VnpReturnViewDTO;
import com.paymentservice.core.entity.Payment;
import com.paymentservice.core.enums.ErrorCode;
import com.paymentservice.core.enums.PaymentProvider;
import com.paymentservice.core.enums.PaymentStatus;
import com.paymentservice.core.enums.VnPayPaymentStatus;
import com.paymentservice.core.event.PaymentResultEvent;
import com.paymentservice.core.exception.AppException;
import com.paymentservice.core.service.PaymentService;
import com.paymentservice.infrastructure.messaging.PaymentResultProducer;
import com.paymentservice.infrastructure.repository.PaymentRepository;
import com.paymentservice.kernel.mapper.PaymentMapper;
import com.paymentservice.kernel.utils.DataUtils;
import com.paymentservice.kernel.utils.VnPaySigner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repo;
    private final VnPayConfig vnp;
    private final PaymentResultProducer producer;

    @Override
    public PaymentChargeResponseDTO charge(PaymentChargeRequestDTO req) {
        if (req == null) throw new AppException(ErrorCode.REQ_BODY_NULL);
        if (DataUtils.isNull(req.getRentalId())) throw new AppException(ErrorCode.REQ_ID_REQUIRED);
        if (DataUtils.isNull(req.getAmount()) || req.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new AppException(ErrorCode.REQ_INVALID_AMOUNT);
        if (DataUtils.isBlank(req.getMethod()))
            throw new AppException(ErrorCode.PAYMENT_METHOD_REQUIRED);
        if (DataUtils.isBlank(req.getProvider()))
            throw new AppException(ErrorCode.PAYMENT_PROVIDER_REQUIRED);
        if (!PaymentProvider.VNPAY.name().equalsIgnoreCase(req.getProvider()))
            throw new AppException(ErrorCode.PAYMENT_PROVIDER_UNSUPPORTED);
        if (DataUtils.isBlank(req.getCurrency())) req.setCurrency("VND");

        // tạo Payment pending
        String paymentCode = UUID.randomUUID().toString();
        Payment p = Payment.builder()
                .paymentCode(paymentCode)
                .userId(req.getUserId())
                .rentalId(req.getRentalId())
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .method(req.getMethod())
                .provider(req.getProvider())
                .status(PaymentStatus.STATUS_PENDING.getStatus())
                .build();
        p = repo.save(p);


        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Map<String,String> params = new LinkedHashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnp.getTmnCode());
        params.put("vnp_Amount", String.valueOf(req.getAmount().multiply(BigDecimal.valueOf(100)).longValue()));
        params.put("vnp_OrderType", "other");
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", p.getPaymentCode());
        params.put("vnp_OrderInfo", "Rental " + req.getRentalId());
        params.put("vnp_Locale", "vn");
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_ReturnUrl", vnp.getReturnUrl());
        params.put("vnp_CreateDate", now);
        params.put("vnp_ExpireDate", LocalDateTime.now().plusMinutes(15)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        try {
            String secureHash = VnPaySigner.sign(params, vnp.getHashSecret());
            params.put("vnp_SecureHashType", "HmacSHA512");
            params.put("vnp_SecureHash", secureHash);
        } catch (Exception e) {
            throw new AppException(ErrorCode.PAYMENT_BUILD_URL_FAILED, e.getMessage());
        }
        String qs = VnPaySigner.buildQueryString(params);
        String checkoutUrl = vnp.getPayUrl() + "?" + qs;

        return PaymentMapper.toChargeResponse(p, checkoutUrl);
    }

    @Override
    public PaymentDTO getByCode(String paymentCode) {
        Payment p = repo.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        return PaymentMapper.toDTO(p);
    }

    @Override
    public void handleVnpReturn(VnpReturnRequestDTO req) {
        Map<String, String> map = new HashMap<>();
        map.put("vnp_TmnCode", req.getVnp_TmnCode());
        map.put("vnp_TxnRef", req.getVnp_TxnRef());
        map.put("vnp_Amount", req.getVnp_Amount());
        map.put("vnp_ResponseCode", req.getVnp_ResponseCode());

        map.put("vnp_OrderInfo", req.getVnp_OrderInfo());
        map.put("vnp_BankCode", req.getVnp_BankCode());
        map.put("vnp_BankTranNo", req.getVnp_BankTranNo());
        map.put("vnp_CardType", req.getVnp_CardType());
        map.put("vnp_PayDate", req.getVnp_PayDate());
        map.put("vnp_TransactionNo", req.getVnp_TransactionNo());
        map.put("vnp_TransactionStatus", req.getVnp_TransactionStatus());

        map.put("vnp_SecureHashType", req.getVnp_SecureHashType());
        map.put("vnp_SecureHash", req.getVnp_SecureHash());

        if (!VnPaySigner.verify(map, vnp.getHashSecret()))
            throw new AppException(ErrorCode.PAYMENT_SIGNATURE_INVALID);

        if (!Objects.equals(req.getVnp_TmnCode(), vnp.getTmnCode()))
            throw new AppException(ErrorCode.PAYMENT_TMN_INVALID);

        Payment p = repo.findByPaymentCode(req.getVnp_TxnRef())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        long vnpAmount = Long.parseLong(req.getVnp_Amount());
        long localAmount = p.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
        if (vnpAmount != localAmount)
            throw new AppException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);

        if (PaymentStatus.STATUS_SUCCEEDED.getStatus().equalsIgnoreCase(p.getStatus())
                || PaymentStatus.STATUS_FAILED.getStatus().equalsIgnoreCase(p.getStatus())) {
            throw new AppException(ErrorCode.PAYMENT_STATUS_FINALIZED);
        }

        if (VnPayPaymentStatus.SUCCESS.getCode().equals(req.getVnp_ResponseCode())) {
            p.setStatus(PaymentStatus.STATUS_SUCCEEDED.getStatus());
            p.setProviderTxnId(req.getVnp_TransactionNo());
            if (req.getVnp_PayDate() != null) {
                LocalDateTime payAt = LocalDateTime.parse(req.getVnp_PayDate(),
                        DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                p.setPaidAt(payAt);
            } else {
                p.setPaidAt(LocalDateTime.now());
            }
        } else {
            p.setStatus(PaymentStatus.STATUS_FAILED.getStatus());
        }
        repo.save(p);


        PaymentResultEvent paymentResultEvent;
        if ((VnPayPaymentStatus.SUCCESS.getCode()).equals(req.getVnp_ResponseCode())) {
            paymentResultEvent = PaymentResultEvent.builder()
                    .rentalId(Integer.valueOf(req.getVnp_OrderInfo().substring(7)))
                    .result(PaymentStatus.STATUS_SUCCEEDED.getStatus())
                    .build();
        } else {
            paymentResultEvent = PaymentResultEvent.builder()
                    .rentalId(Integer.valueOf(req.getVnp_OrderInfo().substring(7)))
                    .result(PaymentStatus.STATUS_FAILED.getStatus())
                    .build();
        }
        producer.sendPaymentResult(paymentResultEvent);
    }
}
