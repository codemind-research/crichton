package crichton.domain.services;

import crichton.application.exceptions.CustomException;
import crichton.application.exceptions.code.FailedErrorCode;
import crichton.domain.dtos.TestDTO;
import crichton.enumeration.TestResult;
import crichton.runner.ProgressRunner;
import crichton.runner.RunResult;
import crichton.runner.UnitTestRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import runner.PluginRunner;

import java.io.File;
import java.util.Optional;

@Service("TestService")
public class TestServiceImpl implements TestService{

    @Override
    public TestDTO.TestResponse doUnitTest(String sourcePath, MultipartFile settings) throws CustomException {
        if (!isCheckSourcePath(sourcePath))
            throw new CustomException(FailedErrorCode.NOT_EXIST_DIRECTORY);
        TestResult isUnitTestDone = runUnitTest(sourcePath, settings);
        return TestDTO.TestResponse.builder()
                                   .testResult(isUnitTestDone)
                                   .build();
    }

    @Override
    public TestDTO.TestResponse doInjectionTest(MultipartFile binaryFile, int testDuration){
        TestResult isInjectionTestDone = runInjectionTest(binaryFile, testDuration);
        return TestDTO.TestResponse.builder()
                                   .testResult(isInjectionTestDone)
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

    private TestResult runUnitTest(String sourcePath, MultipartFile settings){
        try {
            UnitTestRunner runner = new UnitTestRunner(sourcePath, Optional.ofNullable(settings));
            runner.run().map(RunResult::isSuccess).orElseThrow(NoSuchFieldException::new);
            return runner.isSuccessUnitTest() ? TestResult.SUCCESS : TestResult.FAILURE;
        } catch (Exception e) {
            return TestResult.FAILURE;
        }
    }

    private TestResult runInjectionTest(MultipartFile binaryFile, int testDuration) {
        return TestResult.FAILURE;
    }


    private boolean isCheckSourcePath(String sourcePath) {
        return !sourcePath.isBlank() && new File(sourcePath).exists();
    }

}
