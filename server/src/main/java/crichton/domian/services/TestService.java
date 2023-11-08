package crichton.domian.services;

import crichton.application.exceptions.TestFailedException;

public interface TestService {

    void doUnitTest() throws TestFailedException;

    String doInjectionTest();

}
