package crichton.application.controllers;

import crichton.application.exceptions.CustomException;
import crichton.domain.dtos.TestDTO;
import crichton.domain.services.TestService;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import runner.PluginRunner;

@CrossOrigin
@RestController("TestController")
@RequestMapping("/api/v1/crichton/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/plugin")
    @ApiOperation(value = "플러그인 설정", notes = "클라이언트에서 필요한 플러그인 설정 정보를 보내주는 Api")
    public ResponseEntity<TestDTO.PluginResponse> getPlugin() throws CustomException {
        TestDTO.PluginResponse response = testService.getPlugin();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/plugin/run")
    @ApiOperation(value = "플러그인 테스트 시작", notes = "플러그인 테스트를 시작하는 Api")
    public ResponseEntity<TestDTO.TestResponse> doPluginTest(@RequestPart(value="data") TestDTO.PluginRequest request,
                                                           @RequestPart(value="file", required = false) MultipartFile pluginSettings)  throws CustomException {
        TestDTO.TestResponse response = testService.doPluginTest(request, pluginSettings);
        return ResponseEntity.ok(response);
    }

}