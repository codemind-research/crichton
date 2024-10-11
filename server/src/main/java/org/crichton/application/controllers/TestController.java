package org.crichton.application.controllers;

import org.crichton.application.exceptions.CustomException;
import org.crichton.models.dtos.LogDTO;
import org.crichton.models.dtos.TestDTO;
import org.crichton.domain.services.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Test Controller", description = "This is an API for testing plugin-related functionalities.")
@CrossOrigin
@RestController("TestController")
@RequestMapping("/api/v1/crichton/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/plugin")
    @Operation(summary = "Installed Plugin List",
            description = "Retrieves the list of installed plugins on the server. This API is essential for obtaining " +
                    "comprehensive information about the currently installed plugins, including their configurations " +
                    "and functionalities. It plays a crucial role in enabling communication between the client and " +
                    "server, facilitating seamless integration and interaction with various plugins.")
    public ResponseEntity<TestDTO.PluginResponse> getPlugin() throws CustomException {
        TestDTO.PluginResponse response = testService.getPlugin();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/plugin/run", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Start Plugin Testing", description = "Initiates plugin testing, confirming functionality and results " +
            "by conveying detailed test information and configurations.")
    public ResponseEntity<TestDTO.TestResponse> doPluginTest(@RequestPart(value="data") TestDTO.PluginRequest request,
                                                           @RequestPart(value="file", required = false) MultipartFile pluginSettings)  throws CustomException {
        TestDTO.TestResponse response = testService.doPluginTest(request, pluginSettings);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/log")
    @Operation(summary = "Plugin Logs", description = "Retrieves plugin log information through the 'Plugin Log Retrieval API.'")
    public ResponseEntity<LogDTO.LogResponse> getCrichtonLog(@RequestBody LogDTO.LogRequest request){
        LogDTO.LogResponse response = testService.getCrichtonLog(request);
        return ResponseEntity.ok(response);
    }


}