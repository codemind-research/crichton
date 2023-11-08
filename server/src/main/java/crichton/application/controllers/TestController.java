package crichton.application.controllers;

import crichton.application.exceptions.TestFailedException;
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

    @GetMapping("/unit")
    @ApiOperation(value = "단위 테스트", notes = "자동 단위 테스트를 시작하는 Api")
    public ResponseEntity<TestDTO.TestResponse> doUnitTest() throws TestFailedException {
        testService.doUnitTest();
        return ResponseEntity.ok(TestDTO.TestResponse.builder().message("???????").build());
    }

    @GetMapping("/injection")
    @ApiOperation(value = "결함 테스트", notes = "결함 테스트를 시작하는 Api")
    public String doInjectionTest() {
        return "추후 결함 테스트 준비";
    }

}