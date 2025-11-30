package com.rentalservice.kernel.client;

import com.rentalservice.core.constant.enums.ErrorCode;
import com.rentalservice.core.constant.exception.AppException;
import com.rentalservice.core.constant.response.GeneralResponse;
import com.rentalservice.core.constant.response.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
public class RestClientTemplate {

    private final RestTemplate restTemplate;

    public RestClientTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static RestClientTemplate withTimeouts(Duration connectTimeout, Duration readTimeout) {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        if (connectTimeout != null) f.setConnectTimeout((int) connectTimeout.toMillis());
        if (readTimeout != null) f.setReadTimeout((int) readTimeout.toMillis());
        return new RestClientTemplate(new RestTemplate(f));
    }

    /* ===================== Object response ===================== */

    public <T> T exchangeForObject(
            String url,
            HttpMethod method,
            Object body,
            HttpHeaders headers,
            ParameterizedTypeReference<GeneralResponse<T>> responseType
    ) {
        HttpHeaders h = ensureJson(headers);
        HttpEntity<Object> entity = new HttpEntity<>(body, h);
        ResponseEntity<GeneralResponse<T>> resp =
                restTemplate.exchange(url, method, entity, responseType);

        GeneralResponse<T> gr = requireBody(resp);
        ensureOk(gr.getStatus());
        return gr.getData();
    }

    public <T> T getForObject(
            String url,
            HttpHeaders headers,
            ParameterizedTypeReference<GeneralResponse<T>> responseType
    ) {
        return exchangeForObject(url, HttpMethod.GET, null, headers, responseType);
    }

    public <T> T postForObject(
            String url,
            Object body,
            HttpHeaders headers,
            ParameterizedTypeReference<GeneralResponse<T>> responseType
    ) {
        return exchangeForObject(url, HttpMethod.POST, body, headers, responseType);
    }

    /* ===================== List response ===================== */

    public <T> List<T> exchangeForList(
            String url,
            HttpMethod method,
            Object body,
            HttpHeaders headers,
            ParameterizedTypeReference<GeneralResponse<List<T>>> responseType
    ) {
        log.info("exchangeForList {} {} {}", url, body, headers);
        HttpHeaders h = ensureJson(headers);
        HttpEntity<Object> entity = new HttpEntity<>(body, h);
        ResponseEntity<GeneralResponse<List<T>>> resp =
                restTemplate.exchange(url, method, entity, responseType);

        GeneralResponse<List<T>> gr = requireBody(resp);
        ensureOk(gr.getStatus());
        List<T> data = gr.getData();
        return data == null ? Collections.emptyList() : data;
    }

    public <T> List<T> getForList(
            String url,
            HttpHeaders headers,
            ParameterizedTypeReference<GeneralResponse<List<T>>> responseType
    ) {
        return exchangeForList(url, HttpMethod.GET, null, headers, responseType);
    }

    public <T> List<T> postForList(
            String url,
            Object body,
            HttpHeaders headers,
            ParameterizedTypeReference<GeneralResponse<List<T>>> responseType
    ) {
        log.info("postForList {} {} {}", url, body, headers);
        return exchangeForList(url, HttpMethod.POST, body, headers, responseType);
    }

    /* ===================== Helpers ===================== */

    private static HttpHeaders ensureJson(HttpHeaders headers) {
        HttpHeaders h = (headers == null) ? new HttpHeaders() : HttpHeaders.readOnlyHttpHeaders(headers).toSingleValueMap().isEmpty()
                ? headers : new HttpHeaders(headers);
        if (!h.containsKey(HttpHeaders.CONTENT_TYPE)) {
            h.setContentType(MediaType.APPLICATION_JSON);
        }
        return h;
    }

    private static <X> GeneralResponse<X> requireBody(ResponseEntity<GeneralResponse<X>> resp) {
        GeneralResponse<X> body = resp.getBody();
        if (body == null) {
            throw new AppException(ErrorCode.SYS_UNEXPECTED, "Empty body from remote service");
        }
        return body;
    }

    private static void ensureOk(Status status) {
        if (status == null || status.getCode() != ErrorCode.SUCCESS.getCode()) {
            int code = status == null ? ErrorCode.SYS_UNEXPECTED.getCode() : status.getCode();
            String msg = status == null ? "Unknown error" : status.getDisplayMessage();
            throw new AppException(ErrorCode.SYS_UNEXPECTED, "Remote error [" + code + "]: " + msg);
        }
    }
}
