package crichton.domian.services;

import crichton.application.exceptions.CustomException;
import crichton.domian.dtos.TestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface TestService {

    TestDTO.TestResponse doUnitTest(String sourcePath, MultipartFile settings) throws CustomException;

    String getProgress() throws CustomException;

}
