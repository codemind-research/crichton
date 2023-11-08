package crichton.domian.services;

import crichton.application.exceptions.TestFailedException;
import org.springframework.stereotype.Service;

@Service("TestService")
public class TestServiceImpl implements TestService{

    @Override
    public void doUnitTest() throws TestFailedException {
        try{
            
        }catch (Exception e){
            throw new TestFailedException();
        }
    }

    @Override
    public String doInjectionTest() {
        return null;
    }
}
