package ru.fakel.smile.file.hosting.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Error {
    NOT_CORRECT_FILE_NAME(1, "File name is not correct"),
    INTERNAL_SERVER_ERROR(2, "Internal server error");

    private final int code;
    private final String description;
}
