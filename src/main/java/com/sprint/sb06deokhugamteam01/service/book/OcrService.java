package com.sprint.sb06deokhugamteam01.service.book;

public interface OcrService {

    String extractIsbnFromImage(byte[] imageBytes, String fileType);

}
