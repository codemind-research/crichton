package crichton.domain.services;

import crichton.application.exceptions.CustomException;
import crichton.domain.dtos.LogDTO;
import crichton.domain.dtos.TestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface TestService {

    TestDTO.TestResponse doPluginTest(TestDTO.PluginRequest request, MultipartFile settings) throws CustomException;

    TestDTO.PluginResponse getPlugin() throws CustomException;

    LogDTO.LogResponse getCrichtonLog(LogDTO.LogRequest request);

}
