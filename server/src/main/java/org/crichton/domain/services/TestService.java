package org.crichton.domain.services;

import org.crichton.application.exceptions.CustomException;
import org.crichton.domain.dtos.LogDTO;
import org.crichton.domain.dtos.TestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface TestService {

    TestDTO.TestResponse doPluginTest(TestDTO.PluginRequest request, MultipartFile settings) throws CustomException;

    TestDTO.PluginResponse getPlugin() throws CustomException;

    LogDTO.LogResponse getCrichtonLog(LogDTO.LogRequest request);

}
