package ru.fakel.smile.file.hosting.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.fakel.smile.file.hosting.dto.FileUploadResponseDto;
import ru.fakel.smile.file.hosting.errors.Error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileStorageService {
    private final static String bucketName = "testbucket";
    private final static String keyName = "files/";
    private final static String NOT_CORRECT_FILE_NAME = "..";

    private final AmazonS3 amazonS3Client;


    public FileUploadResponseDto saveFile(@NonNull MultipartFile file) {
        String fileName = file.getOriginalFilename();
        log.debug("FileStorageService#saveFile fileName: {}", fileName);
        try {
            if (fileName == null || fileName.contains(NOT_CORRECT_FILE_NAME)) {
                return FileUploadResponseDto.of(fileName, Error.NOT_CORRECT_FILE_NAME);
            }
            ObjectMetadata data = getObjectMetadata(file);
            amazonS3Client.putObject(bucketName, keyName + fileName, file.getInputStream(), data);
            return FileUploadResponseDto.of(fileName);
        } catch (AmazonServiceException e) {
            log.error(
                    "Error when try make request to Amazon S3, request was rejected. HTTP Status Code: {} AWS Error Code: {} Error Type: {} Request ID: {}",
                    e.getStatusCode(), e.getErrorCode(), e.getErrorType(), e.getRequestId(), e);
            return FileUploadResponseDto.of(fileName, Error.INTERNAL_SERVER_ERROR);
        } catch (AmazonClientException e) {
            log.error("Error when try to connect to Amazon S3", e);
            return FileUploadResponseDto.of(fileName, Error.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            log.error("Could not get input stream from file: {}  ", fileName, e);
            return FileUploadResponseDto.of(fileName, Error.INTERNAL_SERVER_ERROR);
        }

    }

    public List<FileUploadResponseDto> saveFiles(@NonNull MultipartFile[] files) {
        log.debug("FileStorageService#saveFiles");
        return Arrays.stream(files).parallel()
                .map(this::saveFile).collect(Collectors.toList());
    }

    public Optional<Resource> loadFileAsResource(String fileName) {
        log.debug("FileStorageService#loadFileAsResource fileName: {}", fileName);

        try {
            GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucketName, keyName + fileName);
            S3Object objectPortion = amazonS3Client.getObject(rangeObjectRequest);
            return Optional.of(new InputStreamResource(objectPortion.getObjectContent()));
        } catch (AmazonServiceException e) {
            log.error(
                    "Error when try make request to Amazon S3, request was rejected. HTTP Status Code: {} AWS Error Code: {} Error Type: {} Request ID: {}",
                    e.getStatusCode(), e.getErrorCode(), e.getErrorType(), e.getRequestId(), e);
        } catch (AmazonClientException e) {
            log.error("Error when try to connect to Amazon S3", e);
        }
        return Optional.empty();
    }

    private ObjectMetadata getObjectMetadata(@NonNull MultipartFile file) {
        ObjectMetadata data = new ObjectMetadata();
        data.setContentType(file.getContentType());
        data.setContentLength(file.getSize());
        return data;
    }
}
