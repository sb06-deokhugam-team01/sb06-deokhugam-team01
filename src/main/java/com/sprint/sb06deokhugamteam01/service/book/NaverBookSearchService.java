package com.sprint.sb06deokhugamteam01.service.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import com.sprint.sb06deokhugamteam01.exception.book.BookInfoFetchFailedException;
import com.sprint.sb06deokhugamteam01.exception.book.InvalidIsbnException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
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

        if (result.items.length == 0) {
            throw new InvalidIsbnException(detailMap("isbn", isbn));
        }

        BookData bookData = result.items()[0];

        return BookDto.builder()
                .title(bookData.title)
                .author(bookData.author)
                .description(bookData.description)
                .publisher(bookData.publisher)
                .publishedDate(bookData.getPublishedDate())
                .isbn(bookData.isbn)
                .thumbnailUrl(bookData.image)
                .build();
    }

    private record NaverBookSearchResponse(
            String lastBuildDate,
            int total,
            int start,
            int display,
            BookData[] items
    ) {}

    private record BookData(
            String title,
            String link,
            String image,
            String author,
            String discount,
            String publisher,
            String pubdate,
            String isbn,
            String description
    ) {
        public LocalDate getPublishedDate() {
            if (pubdate.length() != 8) {
                return null;
            }
            return LocalDate.of(
                    Integer.parseInt(pubdate.substring(0, 4)),  // year
                    Integer.parseInt(pubdate.substring(4, 6)),  // month
                    Integer.parseInt(pubdate.substring(6, 8))   // day
            );
        }
    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }

}
