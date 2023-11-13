package crichton.domian.services;

import crichton.application.exceptions.CustomException;
import crichton.domian.dtos.TestDTO;

public interface TestService {

    TestDTO.TestResponse doTest(TestDTO.TestRequest testRequest) throws CustomException;

    String getProgress() throws CustomException;

}
