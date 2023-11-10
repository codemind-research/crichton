package crichton.domian.services;

import crichton.application.exceptions.CustomException;
import crichton.application.exceptions.code.FailedErrorCode;
import crichton.domian.dtos.TestDTO;
import crichton.enumeration.TestResult;
import crichton.runner.CliRunner;
import org.springframework.stereotype.Service;

import java.io.File;

@Service("TestService")
public class TestServiceImpl implements TestService{

    @Override
    public TestDTO.TestResponse doTest(TestDTO.TestRequest testRequest) throws CustomException {
        if (!checkSourcePath(testRequest.getSourcePath()))
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
    public String getLog() throws CustomException {
        try {
            return CliRunner.runProgress();
        } catch (Exception e) {
            throw new CustomException(FailedErrorCode.LOG_READ_FAILED);
        }
    }

    private TestResult runUnitTest(TestDTO.TestRequest testRequest){
        try {
            CliRunner cliRunner = new CliRunner(testRequest.getSourcePath());
            cliRunner.run();
            return new File(cliRunner.getReportPath()).exists() ? TestResult.SUCCESS : TestResult.FAILURE;
        } catch (Exception e) {
            return TestResult.FAILURE;
        }
    }

    private TestResult runInjectionTest(TestDTO.TestRequest testRequest){
        //추후 결함주입테스트 플러그인 추가
        return  TestResult.FAILURE;
    }


    private boolean checkSourcePath(String sourcePath) {
        return !sourcePath.isBlank() && new File(sourcePath).exists();
    }

}
