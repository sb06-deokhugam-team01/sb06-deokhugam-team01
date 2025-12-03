package com.sprint.sb06deokhugamteam01.service.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.sb06deokhugamteam01.exception.book.BookInfoFetchFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrSpaceService implements OcrService{

    @Value("${ocr.space.api.endpoint}")
    private String ocrSpaceApiEndpoint;
    @Value("${ocr.space.api.key}")
    private String apiKey;

    @Override
    public String extractIsbnFromImage(byte[] image, String fileType) {

        RestClient restClient = RestClient.builder()
                .baseUrl(ocrSpaceApiEndpoint)
                .build();

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("base64Image", "data:image/" + fileType.toLowerCase() + ";base64," +
                Base64.getEncoder().encodeToString(image));
        bodyBuilder.part("apikey", apiKey);
        bodyBuilder.part("filetype", fileType);

        OcrResult ocrResult = restClient.post()
                .header("apikey", apiKey)
                .body(bodyBuilder.build())
                .retrieve()
                .body(OcrResult.class);

        if (ocrResult != null && ocrResult.parsedResults.length > 0) {
            return parseIsbnFromOcrResult(ocrResult.parsedResults[0].parsedText);
        } else {
            throw new BookInfoFetchFailedException(detailMap("message", "OCR processing failed or returned no results."));
        }
    }

    static Pattern ISBN_PATTERN = Pattern.compile("(97[89][0-9A-Za-z\\-•.]+)");

    private String parseIsbnFromOcrResult(String input) {

        Matcher matcher = ISBN_PATTERN.matcher(input);

        List<String> list = new ArrayList<>();

        while (matcher.find()) {
            list.add(matcher.group(1));
        }

        if (list.isEmpty()) {
            throw new BookInfoFetchFailedException(detailMap("message", "No ISBN found in OCR result."));
        }

        return list.get(0);

    }

    public record OcrResult(
            @JsonProperty("ParsedResults")
            ParsedResult[] parsedResults,

            @JsonProperty("OCRExitCode")
            int ocrExitCode,

            @JsonProperty("IsErroredOnProcessing")
            boolean isErroredOnProcessing,

            @JsonProperty("ProcessingTimeInMilliseconds")
            double processingTimeInMilliseconds,

            @JsonProperty("SearchablePDFURL")
            String searchablePDFURL
    ) {}

    public record ParsedResult(
            @JsonProperty("Overlay")
            String overlay,

            @JsonProperty("FileParseExitCode")
            int fileParseExitCode,

            @JsonProperty("TextOrientation")
            String textOrientation,

            @JsonProperty("ParsedText")
            String parsedText,

            @JsonProperty("ErrorMessage")
            String errorMessage,

            @JsonProperty("ErrorDetails")
            String errorDetails
    ) {}

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }

}
