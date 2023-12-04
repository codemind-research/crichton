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

    @PostMapping("/unit/run")
    @ApiOperation(value = "단위 테스트 시작", notes = "자동 단위 테스트를 시작하는 Api")
    public ResponseEntity<TestDTO.TestResponse> doUnitTest(@RequestPart(value="data") TestDTO.UnitTestRequest request,
                                                           @RequestPart(value="file", required = false) MultipartFile settings)  throws CustomException {
        TestDTO.TestResponse response = testService.doUnitTest(request.getSourcePath(), settings);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/injection/run")
    @ApiOperation(value = "결함 주입 테스트 시작", notes = "결함 주입 테스트를 시작하는 Api")
    public ResponseEntity<TestDTO.TestResponse> doInjectionTest(@RequestPart(value="data") TestDTO.InjectionTestRequest request,
                                                                @RequestPart(value="file", required = false) MultipartFile binaryFile){
        TestDTO.TestResponse response = testService.doInjectionTest(binaryFile, request.getTestDuration());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/progress")
    @ApiOperation(value = "단위 테스트 프로그레스 정보 가져오기")
    public ResponseEntity<TestDTO.ProgressResponse> getProgress() throws CustomException {
        String progress = testService.getProgress();
        return ResponseEntity.ok(TestDTO.ProgressResponse.builder()
                                                    .progress(progress)
                                                    .build());
    }

}