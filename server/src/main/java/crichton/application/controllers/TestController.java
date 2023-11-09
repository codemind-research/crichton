package crichton.application.controllers;

import crichton.application.exceptions.CustomException;
import crichton.domian.dtos.TestDTO;
import crichton.domian.services.TestService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;

@CrossOrigin
@RestController("TestController")
@RequestMapping("/api/v1/crichton/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/unit")
    @ApiOperation(value = "단위 테스트", notes = "자동 단위 테스트를 시작하는 Api")
    public ResponseEntity<TestDTO.TestResponse> doUnitTest(@RequestBody TestDTO.TestRequest request) throws CustomException {
        testService.doUnitTest(request.getSourcePath());
        //TODO: reportPath 추후 개선
        String reportPath = Paths.get(System.getProperty("user.home"),"coyoteCli","report").toString();
        return ResponseEntity.ok(TestDTO.TestResponse.builder().reportPath(reportPath).build());
    }

    @GetMapping("/injection")
    @ApiOperation(value = "결함 테스트", notes = "결함 테스트를 시작하는 Api")
    public String doInjectionTest() {
        return "injectionTest";
    }

}