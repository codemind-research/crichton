package crichton.application.controllers;

import crichton.application.exceptions.CustomException;
import crichton.domian.dtos.TestDTO;
import crichton.domian.services.TestService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController("TestController")
@RequestMapping("/api/v1/crichton/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/run")
    @ApiOperation(value = "테스트 시작", notes = "자동 테스트를 시작하는 Api")
    public ResponseEntity<TestDTO.TestResponse> doTest(@RequestBody TestDTO.TestRequest request) throws CustomException {
        TestDTO.TestResponse response = testService.doTest(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/progress")
    @ApiOperation(value = "테스트 프로그레스 정보 가져오기")
    public ResponseEntity<TestDTO.ProgressResponse> getProgress() throws CustomException {
        String progress = testService.getProgress();
        return ResponseEntity.ok(TestDTO.ProgressResponse.builder()
                                                    .progress(progress)
                                                    .build());
    }

}