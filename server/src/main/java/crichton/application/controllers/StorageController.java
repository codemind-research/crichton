package crichton.application.controllers;

import crichton.application.exceptions.CustomException;
import crichton.domain.dtos.StorageDTO;
import crichton.domain.services.StorageService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@CrossOrigin
@RestController("StorageController")
@RequestMapping("/api/v1/crichton/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload")
    @ApiOperation(value = "파일 업로드", notes = "클라이언트에서 서버로 소스코드를 전달하는 Api")
    public ResponseEntity<StorageDTO.StorageResponse> uploadFile(@RequestPart("file")MultipartFile file) throws CustomException {
        File unzipPath = storageService.uploadFile(file);
        return ResponseEntity.ok(StorageDTO.StorageResponse.builder().unzipPath(unzipPath.getAbsolutePath())
                                                           .build());
    }

}
