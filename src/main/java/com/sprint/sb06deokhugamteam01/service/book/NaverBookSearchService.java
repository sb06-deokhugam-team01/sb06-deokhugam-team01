package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.exception.book.BookInfoFetchFailedException;
import com.sprint.sb06deokhugamteam01.exception.book.InvalidIsbnException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverBookSearchService implements BookSearchService{

    private String naverApiEndpoint = "https://openapi.naver.com/v1/search/book_adv.json";
    private String apiClientId;
    private String apiClientSecret;

    @Override
    public BookDto searchBookByIsbn(String isbn) {

        RestClient restClient = RestClient.builder()
                .baseUrl(naverApiEndpoint)
                .build();

        String result = restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("d_isbn", isbn)
                        .build())
                .header("X-Naver-Client-Id", apiClientId)
                .header("X-Naver-Client-Secret", apiClientSecret)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new InvalidIsbnException(detailMap("isbn", isbn));
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new BookInfoFetchFailedException(detailMap("isbn", isbn));
                }))
                .body(String.class);

        log.info("Naver Book Search API response: {}", result);

        return null;
    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }

}
