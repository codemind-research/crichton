package crichton.application.controllers;

import crichton.application.exceptions.CustomException;
import crichton.domain.dtos.StorageDTO;
import crichton.domain.services.StorageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Tag(name = "Storage Controller", description = "This API controller is used for transferring files between the client and the server.")
@CrossOrigin
@RestController("StorageController")
@RequestMapping("/api/v1/crichton/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Source Code Upload API", description = "The 'Source Code Upload API' facilitates the transfer of source code from the" +
            " client to the server. This API endpoint is crucial for enabling users to upload and submit their source code effortlessly. " +
            "It supports various file formats and ensures a seamless process for developers to contribute their code to the server.")
    public ResponseEntity<StorageDTO.StorageResponse> uploadFile(@RequestPart("file")MultipartFile file) throws CustomException {
        File unzipPath = storageService.uploadFile(file);
        return ResponseEntity.ok(StorageDTO.StorageResponse.builder().unzipPath(unzipPath.getAbsolutePath())
                                                           .build());
    }

}
