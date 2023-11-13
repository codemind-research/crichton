package crichton.domian.services;

import crichton.application.exceptions.CustomException;
import crichton.application.exceptions.code.FailedErrorCode;
import crichton.domian.dtos.TestDTO;
import crichton.enumeration.TestResult;
import crichton.runner.ProgressRunner;
import crichton.runner.RunResult;
import crichton.runner.UnitTestRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service("TestService")
public class TestServiceImpl implements TestService{

    @Override
    public TestDTO.TestResponse doTest(TestDTO.TestRequest testRequest) throws CustomException {
        if (!isCheckSourcePath(testRequest.getSourcePath()))
            throw new CustomException(FailedErrorCode.NOT_EXIST_DIRECTORY);

        TestResult isUnitTestDone = testRequest.getUnitTest()
                ? runUnitTest(testRequest) : TestResult.PASS;

        TestResult isInjectionDone = testRequest.getInjectionTest()
                ? runInjectionTest(testRequest) : TestResult.PASS;

        return TestDTO.TestResponse.builder()
                                   .unitTestResult(isUnitTestDone)
                                   .injectionTestResult(isInjectionDone)
                                   .build();
    }

    @Override
    public String getProgress() throws CustomException {
        try {
            ProgressRunner progressRunner = new ProgressRunner();
            Optional<RunResult> result = progressRunner.run();
            return result.orElseGet(result::orElseThrow).getData();
        }catch (Exception e){
            throw new CustomException(FailedErrorCode.LOG_READ_FAILED);
        }
    }

    private TestResult runUnitTest(TestDTO.TestRequest testRequest){
        try {
            UnitTestRunner runner = new UnitTestRunner(testRequest.getSourcePath());
            runner.run();
            return runner.isSuccessUnitTest() ? TestResult.SUCCESS : TestResult.FAILURE;
        } catch (Exception e) {
            return TestResult.FAILURE;
        }
    }

    private TestResult runInjectionTest(TestDTO.TestRequest testRequest){
        //추후 결함주입테스트 플러그인 추가
        return  TestResult.FAILURE;
    }


    private boolean isCheckSourcePath(String sourcePath) {
        return !sourcePath.isBlank() && new File(sourcePath).exists();
    }

}
