package ru.fakel.smile.file.hosting.dto;

import ru.fakel.smile.file.hosting.errors.Error;

import lombok.Data;

@Data
public class ErrorResponseDto {
    private final int code;
    private final String description;

    public static ErrorResponseDto of(Error error) {
        return new ErrorResponseDto(error.getCode(), error.getDescription());
    }
}
