package com.sprint.sb06deokhugamteam01.service.book;

import com.sprint.sb06deokhugamteam01.dto.book.naver.BookData;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.dto.book.naver.NaverBookSearchResponse;
import com.sprint.sb06deokhugamteam01.exception.book.BookInfoFetchFailedException;
import com.sprint.sb06deokhugamteam01.exception.book.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverBookSearchService implements BookSearchService{

    @Value("${naver.api.endpoint}")
    private String naverApiEndpoint;
    @Value("${naver.api.client-id}")
    private String apiClientId;
    @Value("${naver.api.client-secret}")
    private String apiClientSecret;

    @Override
    public BookDto searchBookByIsbn(String isbn) {

        RestClient restClient = RestClient.builder()
                .baseUrl(naverApiEndpoint)
                .build();

        NaverBookSearchResponse result = restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("d_isbn", isbn)
                        .build())
                .header("X-Naver-Client-Id", apiClientId)
                .header("X-Naver-Client-Secret", apiClientSecret)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new BookInfoFetchFailedException(detailMap("isbn", isbn));
                }))
                .body(NaverBookSearchResponse.class);

        if (result.items().length == 0) {
            throw new BookNotFoundException(detailMap("isbn", isbn));
        }

        BookData bookData = result.items()[0];

        return BookDto.builder()
                .title(bookData.title())
                .author(bookData.author())
                .description(bookData.description())
                .publisher(bookData.publisher())
                .publishedDate(bookData.getPublishedDate())
                .isbn(bookData.isbn())
                .thumbnailUrl(fetchImageData(bookData.image()))
                .build();
    }

    private String fetchImageData(String imageUrl) {
        RestClient restClient = RestClient.builder()
                .baseUrl(imageUrl)
                .build();

        byte[] imageData = restClient.get()
                .retrieve()
                .body(byte[].class);

        return Base64.getEncoder().encodeToString(imageData);

    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }

}
