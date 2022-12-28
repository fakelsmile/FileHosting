package ru.fakel.smile.file.hosting.dto;

import ru.fakel.smile.file.hosting.errors.Error;

import lombok.Data;

@Data
public class FileUploadResponseDto {

    private final String fileName;
    private final ErrorResponseDto error;

    public static FileUploadResponseDto of(String fileName, Error error) {
        return new FileUploadResponseDto(fileName, ErrorResponseDto.of(error));
    }

    public static FileUploadResponseDto of(String fileName) {
        return new FileUploadResponseDto(fileName, null);
    }
}
