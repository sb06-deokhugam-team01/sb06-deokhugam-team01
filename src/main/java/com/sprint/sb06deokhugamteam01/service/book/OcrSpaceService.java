package com.sprint.sb06deokhugamteam01.service.book;

import lombok.Builder;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

public class OcrSpaceService implements OcrService{

    private String ocrSpaceApiEndpoint = "https://api.ocr.space/parse/image";
    private String apiKey;

    @Override
    public String extractIsbnFromImage(byte[] imageBytes, String fileType) {

        RestClient restClient = RestClient.builder()
                .baseUrl(ocrSpaceApiEndpoint)
                .build();

        String result = restClient.post()
                .header("apikey", apiKey)
                .body(ApiRequest.builder()
                        .apiKey(apiKey)
                        .base64Image("data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(imageBytes))
                        .fileType(fileType)
                        .build())
                .retrieve()
                .body(String.class);

        return "";
    }

    @Builder
    private record ApiRequest(
        String apiKey,
        String base64Image,
        String fileType
    ){

    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }

}
