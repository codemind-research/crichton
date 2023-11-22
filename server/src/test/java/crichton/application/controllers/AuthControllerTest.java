package crichton.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import crichton.domain.dtos.AuthDTO;
import crichton.domain.services.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Test
    void getToken() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/crichton/auth/token"))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void deleteRefreshToken() throws Exception{
        String userId = UUID.randomUUID().toString();
        refreshTokenService.generateRefreshToken(userId);
        AuthDTO.TokenRequest tokenRequest = new AuthDTO.TokenRequest(userId);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/crichton/auth/token")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(mapper.writeValueAsString(tokenRequest)))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print());
    }

}