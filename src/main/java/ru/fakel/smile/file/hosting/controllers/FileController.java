package ru.fakel.smile.file.hosting.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ru.fakel.smile.file.hosting.dto.FileUploadResponseDto;
import ru.fakel.smile.file.hosting.services.FileStorageService;
import ru.fakel.smile.file.hosting.utils.ContentTypeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.ATTACHMENT;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileUploadResponseDto> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileStorageService.saveFile(file));
    }

    @PostMapping(value = "/upload/list", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        return ResponseEntity.ok(fileStorageService.saveFiles(files));
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        return fileStorageService.loadFileAsResource(fileName)
                .map(resource -> {
                    String contentType = ContentTypeUtil.getContentType(request, resource);
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    ATTACHMENT + "; filename=\"" + resource.getFilename() + "\"")
                            .body(resource);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when try download file: " + fileName));
    }
}
