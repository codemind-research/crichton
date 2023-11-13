package crichton.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.application.exceptions.handler.GlobalExceptionResponse;
import crichton.domian.dtos.TestDTO;
import crichton.enumeration.TestResult;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    private String sourcePath;

    @BeforeEach
    void sourcePathInit() {
        sourcePath = Paths.get(System.getProperty("user.home"),"git","crichton","tests","c++-samples").toString();
    }


    @Test
    @Order(1)
    void doUnitTestBlankException() throws Exception {
        TestDTO.TestRequest request = new TestDTO.TestRequest();
        request.setSourcePath("");
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/test/run")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(mapper.writeValueAsString(request)))
               .andExpect(MockMvcResultMatchers.status().is5xxServerError())
               .andDo(print())
               .andReturn()
               .getResponse()
               .getContentAsString()
               .replaceAll("^\"|\"$", "");

      GlobalExceptionResponse response = mapper.readValue(result ,GlobalExceptionResponse.class);
      assertEquals("F001", response.getCode());
    }

    @Test
    @Order(2)
    void doUnitTestNotFileExistException() throws Exception {
        TestDTO.TestRequest request = new TestDTO.TestRequest();
        request.setSourcePath("%2ka1oll");
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/test/run")
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(mapper.writeValueAsString(request)))
                               .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                               .andDo(print())
                               .andReturn()
                               .getResponse()
                               .getContentAsString()
                               .replaceAll("^\"|\"$", "");

        GlobalExceptionResponse response = mapper.readValue(result ,GlobalExceptionResponse.class);
        assertEquals("F001", response.getCode());
    }

    @Test
    @Order(3)
    void doPassTest() throws Exception {
        TestDTO.TestRequest request = new TestDTO.TestRequest();
        request.setSourcePath(sourcePath);
        request.setUnitTest(false);
        request.setInjectionTest(false);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/test/run")
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content(mapper.writeValueAsString(request)))
                               .andExpect(MockMvcResultMatchers.status().isOk())
                               .andDo(MockMvcResultHandlers.print())
                               .andReturn()
                               .getResponse()
                               .getContentAsString()
                               .replaceAll("^\"|\"$", "");
        TestDTO.TestResponse response = mapper.readValue(result ,TestDTO.TestResponse.class);
        assertEquals(TestResult.PASS,response.getUnitTestResult());
        assertEquals(TestResult.PASS,response.getInjectionTestResult());
    }


    @Test
    @Order(4)
    void doUnitTest() throws Exception {
        TestDTO.TestRequest request = new TestDTO.TestRequest();
        request.setSourcePath(sourcePath);
        request.setUnitTest(true);
        request.setInjectionTest(false);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/crichton/test/run")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(mapper.writeValueAsString(request)))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print())
               .andReturn()
               .getResponse()
               .getContentAsString()
               .replaceAll("^\"|\"$", "");
        TestDTO.TestResponse response = mapper.readValue(result ,TestDTO.TestResponse.class);
        assertEquals(TestResult.SUCCESS,response.getUnitTestResult());
        assertEquals(TestResult.PASS,response.getInjectionTestResult());
    }
    
    @Test
    @Order(5)
    void getProgress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/test/progress"))
                               .andExpect(MockMvcResultMatchers.status().isOk())
                               .andDo(MockMvcResultHandlers.print());
    }
}