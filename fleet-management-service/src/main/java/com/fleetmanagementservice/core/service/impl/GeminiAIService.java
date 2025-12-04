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
            Bạn là trợ lý AI thông minh cho hệ thống cho thuê xe. Nhiệm vụ của bạn là phân tích câu nói tự nhiên của khách hàng và trích xuất thông tin tìm kiếm xe.
            
            Câu nói của khách hàng: "%s"
            
            Hãy phân tích câu trên và trả về JSON với các trường sau (CHỈ điền những trường bạn TÌM THẤY hoặc SUY LUẬN được):
            
            THÔNG TIN CƠ BẢN:
            - brand (string): Hãng xe - Viết hoa chữ cái đầu. VD: "Toyota", "Honda", "Mazda", "Kia", "Hyundai", "Ford", "Vinfast", "BMW", "Mercedes"
            - model (string): Dòng xe - Viết hoa chữ cái đầu. VD: "Fortuner", "Vios", "City", "Mazda3", "CX5", "Tucson"
            - vehicleType (string): Loại xe - CHỈ dùng: "SEDAN", "SUV", "HATCHBACK", "TRUCK", "VAN", "COUPE"
              * "xe bé", "xe nhỏ gọn" → HATCHBACK
              * "xe 7 chỗ", "xe gia đình" → SUV hoặc VAN
              * "bán tải" → TRUCK
            - seats (integer): Số chỗ ngồi. VD: 2, 4, 5, 7, 9, 16
            - transmission (string): CHỈ "MANUAL" hoặc "AUTOMATIC"
              * "số sàn" → MANUAL
              * "số tự động", "tự động" → AUTOMATIC
            - fuelType (string): CHỈ "GASOLINE", "DIESEL", "ELECTRIC", "HYBRID"
              * "xăng" → GASOLINE
              * "dầu", "diesel" → DIESEL
              * "điện" → ELECTRIC
            - color (string): Màu xe viết THƯỜNG. VD: "white", "black", "red", "blue", "silver", "gray"
            - year (integer): Năm sản xuất >= 1990
            - basePrice: nó kiểu 1000000, 2000000: tức là khi người dùng giả sử hỏi là tìm xe dưới 1000000 thì basePrice truyền vào là 1000000
            tuy nhiên trong trường hợp người dùng bảo là từ 1000000 thì khi đấy vẫn truyền vào basePrice nhưng mà + thêm 1 triệu vào số người dùng nói nhé
            nếu người dùng không nói gì về giá thì bạn cứ không cần include trường này, không cần thêm gì cả
           
            
            METADATA:
            - offset (integer): Số lượng xe khách muốn xem
              * Mặc định: 2
              * Nếu khách nói "cho tôi 3 xe", "xem 5 cái" → điền số đó
              * Tối đa: 5
            - isMeaningful (boolean): 
              * true nếu khách có ý định TÌM/THUÊ xe (VD: "cần xe", "tìm xe", "muốn thuê", "cho tôi xe")
              * false nếu chỉ chào hỏi, hỏi giá, hỏi thông tin chung (VD: "xin chào", "giá bao nhiêu", "có mấy xe")
             
            lưu ý là 2 cái trong metadata bắt buộc phải có khi bạn trả về, tức là bình thường offset sẽ là 2, isMeaningful sẽ là false nếu không có thay đổi gì
            tức là nếu bạn ghi đè thì cứ ghi đè, nhưng không thay đổi gì thì cũng phải để trong response trả về
            bạn trả về thì không cần bọc trong metadata, cứ trả về như những trường bình thường khác thôi
            
            TÍNH NĂNG (featureMask):
            Đây là số LONG đại diện cho các tính năng xe cần có. Tính bằng cách CỘNG 2^vị_trí của từng tính năng khách yêu cầu.
            
            DANH SÁCH TÍNH NĂNG (vị trí 0-59):
            0. Hệ thống chống bó cứng phanh ABS
            1. Túi khí an toàn
            2. Cân bằng điện tử / ESC
            3. Cảm biến áp suất lốp / TPMS
            4. Camera lùi / camera hậu
            5. Camera 360 độ / camera toàn cảnh
            6. Cảnh báo điểm mù
            7. Điều hòa tự động / máy lạnh tự động
            8. Ghế bọc da / ghế da
            9. Ghế chỉnh điện / ghế điện
            10. Cửa sổ trời / sunroof
            11. Ga tự động / cruise control
            12. Kết nối Bluetooth / bluetooth
            13. Cổng USB / Type-C
            14. Màn hình cảm ứng / màn hình
            15. Apple CarPlay
            16. Android Auto
            17. Khởi động nút bấm / start-stop button
            18. Chìa khóa thông minh / smart key
            19. Đèn pha tự động / auto headlight
            20. Hỗ trợ giữ làn đường / lane assist
            21. Cảnh báo va chạm phía trước
            22. Cảnh báo phương tiện cắt ngang sau
            23. Hỗ trợ khởi hành ngang dốc / hill assist
            24. Hỗ trợ xuống dốc / downhill assist
            25. Cảm biến lùi xe / cảm biến hậu
            26. Khóa cửa an toàn trẻ em
            27. Điểm neo ghế trẻ em ISOFIX
            28. Ghế sưởi ấm
            29. Ghế thông gió
            30. Ghế nhớ vị trí / memory seat
            31. Tựa lưng chỉnh điện
            32. Điều hòa hàng ghế sau
            33. Lọc không khí
            34. Đèn viền nội thất / ambient light
            35. Sạc không dây / wireless charging
            36. Hộc làm lạnh
            37. Cốp điện / cốp tự động
            38. Hệ thống âm thanh cao cấp / premium sound
            39. Loa siêu trầm / subwoofer
            40. Cổng HDMI
            41. Kết nối điện thoại không dây / wireless mirroring
            42. Màn hình giải trí hàng ghế sau
            43. Đầu đĩa DVD
            44. Radio FM/AM
            45. Điều khiển giọng nói / voice control
            46. Ga tự động thích ứng / adaptive cruise
            47. Đỗ xe tự động / auto parking
            48. Gương chiếu hậu chỉnh điện
            49. Gương gập tự động
            50. Cảm biến mưa / rain sensor
            51. Cảm biến ánh sáng / light sensor
            52. Màn hình hiển thị kính lái HUD
            53. Bảng đồng hồ kỹ thuật số / digital dash
            54. Khởi động bằng nút bấm
            55. Tắt máy tạm thời / idle stop
            56. Cảnh báo áp suất lốp
            57. Phanh tay điện tử
            58. Giữ phanh tự động / auto hold
            59. Lẫy chuyển số / paddle shift
            
            VÍ DỤ TÍNH featureMask:
            - Khách nói: "xe có camera lùi và bluetooth"
              → Camera lùi (vị trí 4) + Bluetooth (vị trí 12)
              → featureMask = 2^4 + 2^12 = 16 + 4096 = 4112
            
            - Khách nói: "cần ABS và cân bằng điện tử"
              → ABS (vị trí 0) + Cân bằng điện tử (vị trí 2)
              → featureMask = 2^0 + 2^2 = 1 + 4 = 5
            
            NGUYÊN TẮC SUY LUẬN:
            - Nếu khách nói "xe gia đình" → seats: 7, vehicleType: "SUV" hoặc "VAN"
            - Nếu khách nói "xe đi phố" → vehicleType: "HATCHBACK", seats: 5
            - Nếu khách nói "xe sang" → có thể suy ra cần nhiều tính năng cao cấp
            - Nếu khách nói "xe an toàn" → suy ra cần ABS, túi khí, ESC, camera
            - CHỈ điền những thông tin bạn CHẮC CHẮN, nếu không chắc thì BỎ QUA
            
            QUY TẮC OUTPUT QUAN TRỌNG:
            1. CHỈ trả về JSON thuần, TUYỆT ĐỐI KHÔNG có ```json hoặc ``` hay markdown
            2. Phải bắt đầu bằng { và kết thúc bằng }
            3. Các giá trị enum phải CHÍNH XÁC như đã nêu
            4. Tên hãng/dòng xe: Viết hoa chữ cái đầu, còn lại thường
            5. Màu xe: viết THƯỜNG toàn bộ
            6. Nếu không tìm thấy thông tin nào thì trả về {}
            
            Bây giờ hãy phân tích câu nói của khách hàng và trả về JSON:
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
            if (node.has("basePrice")) dto.setBasePrice(node.get("basePrice").asDouble());
            if (node.has("isMeaningful")) dto.setIsMeaningful(node.get("isMeaningful").asBoolean());
            if (node.has("featureMask")) dto.setFeatureMask(node.get("featureMask").asLong(0L));
            if (node.has("offset")) dto.setOffset(node.get("offset").asInt());

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
