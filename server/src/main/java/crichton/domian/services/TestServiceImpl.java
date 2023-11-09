package crichton.domian.services;

import crichton.application.exceptions.CustomException;
import crichton.application.exceptions.code.FailedErrorCode;
import crichton.runner.CliRunner;
import org.springframework.stereotype.Service;

@Service("TestService")
public class TestServiceImpl implements TestService{

    @Override
    public void doUnitTest(String sourcePath) throws CustomException {
        CliRunner cliRunner = new CliRunner(sourcePath);
        if (!cliRunner.checkSourcePath())
            throw new CustomException(FailedErrorCode.NOT_EXIST_DIRECTORY);
        try{
            cliRunner.run();
        }catch (Exception e){
            throw new CustomException(FailedErrorCode.UNIT_TEST_FAILED);
        }
    }

    @Override
    public String doInjectionTest() {
        return null;
    }
}
