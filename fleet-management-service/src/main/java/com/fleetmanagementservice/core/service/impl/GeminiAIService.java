package com.fleetmanagementservice.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetmanagementservice.core.constant.enums.ErrorCode;
import com.fleetmanagementservice.core.constant.exception.AppException;
import com.fleetmanagementservice.core.dto.request.VehicleRequestDTO;
import com.fleetmanagementservice.core.service.AIParserService;
import com.fleetmanagementservice.kernel.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiAIService implements AIParserService {
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public VehicleRequestDTO parseVehicleQuery(String freeText) {
        if (DataUtils.isBlank(freeText)) {
            return new VehicleRequestDTO();
        }

        try {
            String prompt = buildPrompt(freeText);
            String jsonResponse = callAI(prompt);
            return parseResponse(jsonResponse);
        } catch (AppException e) {
            log.error("Gemini parsing failed for: {}", freeText, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error parsing freeText: {}", freeText, e);
            throw new AppException(ErrorCode.AI_GEMINI_PARSE_FAILED);
        }
    }

    @Override
    public String buildPrompt(String freeText) {
        return String.format("""
            Phân tích câu tìm kiếm xe sau và trả về JSON:
            
            "%s"
            
            Các field cần parse (chỉ điền field có thông tin):
            - brand: hãng xe, viết hoa chữ cái đầu (Toyota, Honda, Mazda, Kia, Hyundai, Ford, Vinfast...)
            - model: dòng xe, viết hoa chữ cái đầu (Fortuner, Vios, City, Mazda3, CX5, Tucson...)
            - vehicleType: SEDAN, SUV, HATCHBACK, TRUCK, VAN, COUPE
            - seats: số chỗ (integer: 2, 4, 5, 7, 9, 16...)
            - transmission: MANUAL hoặc AUTOMATIC (từ "số sàn", "số tự động")
            - fuelType: GASOLINE, DIESEL, ELECTRIC, HYBRID (từ "xăng", "dầu", "điện", "hybrid")
            - color: màu xe viết thường (white, black, red, blue, silver, gray...)
            - year: năm sản xuất (integer >= 1990)
            - offset: (integer) mặc định sẽ là 2, tuy nhiên nếu người dùng yêu cầu số cụ thể thì điền vào, nhưng cho tối đa là 5 thôi, cái này có nghĩa là người dùng yêu cầu mấy xe ấy
            - isMeaningful: boolean (true/false) cái này thì kiểu bạn sẽ đánh giá xem người dùng có thực sự muốn tìm kiếm xe hay không, nếu câu hỏi người ta thể hiện người ta muốn thuê thì để là true
            
            
            QUY TẮC:
            1. CHỈ trả về JSON thuần, KHÔNG có ```json, KHÔNG có markdown
            2. Bắt đầu bằng { và kết thúc bằng }
            3. Các giá trị enum phải CHÍNH XÁC theo list trên
            4. brand và model: chữ cái đầu viết hoa, còn lại thường
            5. Nếu không chắc chắn field nào thì bỏ qua field đó
            """, freeText);
    }

    @Override
    public String callAI(String prompt) {
        String url = apiUrl + "?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new AppException(ErrorCode.AI_GEMINI_CALL_FAILED);
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            return text.trim();

        } catch (ResourceAccessException e) {
            log.error("Gemini API timeout", e);
            throw new AppException(ErrorCode.AI_GEMINI_TIMEOUT);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini API call failed", e);
            throw new AppException(ErrorCode.AI_GEMINI_CALL_FAILED);
        }
    }

    @Override
    public VehicleRequestDTO parseResponse(String jsonText) {
        try {
            String cleanJson = jsonText.trim();
            if (cleanJson.startsWith("```json")) {
                cleanJson = cleanJson.substring(7);
            }
            if (cleanJson.startsWith("```")) {
                cleanJson = cleanJson.substring(3);
            }
            if (cleanJson.endsWith("```")) {
                cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
            }
            cleanJson = cleanJson.trim();

            JsonNode node = objectMapper.readTree(cleanJson);

            VehicleRequestDTO dto = new VehicleRequestDTO();

            if (node.has("brand")) dto.setBrand(node.get("brand").asText());
            if (node.has("model")) dto.setModel(node.get("model").asText());
            if (node.has("vehicleType")) dto.setVehicleType(node.get("vehicleType").asText());
            if (node.has("seats")) dto.setSeats(node.get("seats").asInt());
            if (node.has("transmission")) dto.setTransmission(node.get("transmission").asText());
            if (node.has("fuelType")) dto.setFuelType(node.get("fuelType").asText());
            if (node.has("color")) dto.setColor(node.get("color").asText());
            if (node.has("year")) dto.setYear(node.get("year").asInt());

            return dto;
        } catch (Exception e) {
            log.error("Failed to parse JSON: {}", jsonText, e);
            throw new AppException(ErrorCode.AI_GEMINI_PARSE_FAILED);
        }
    }

    @Override
    public boolean validateCredentials() {
        return DataUtils.nonNull(apiKey) && !apiKey.isBlank();
    }

    @Override
    public String getProviderName() {
        return "GEMINI";
    }
}
