package crichton.domian.services;

import crichton.application.exceptions.CustomException;
import crichton.domian.dtos.TestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface TestService {

    TestDTO.TestResponse doUnitTest(String sourcePath, MultipartFile settings) throws CustomException;

    TestDTO.TestResponse doInjectionTest(MultipartFile binaryFile, int testDuration);

    String getProgress() throws CustomException;

}
