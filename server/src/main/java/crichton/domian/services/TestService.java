package crichton.domian.services;

import crichton.application.exceptions.CustomException;

public interface TestService {

    void doUnitTest(String sourcePath) throws CustomException;

    String doInjectionTest();

}
