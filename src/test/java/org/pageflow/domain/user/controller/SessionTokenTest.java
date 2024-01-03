package org.pageflow.domain.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : sechan
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SessionTokenTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void jwtAuthorization() throws Exception {
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlkIjoxLCJhdXRoIjoiUk9MRV9BRE1JTiIsImlhdCI6MTcwMzY2OTQxMywiZXhwIjoxNzAzNjcxMjEzfQ.mNa-fX8o_FYk1Rap-yvQdFRCypneEqyZknFplBDK0jM";
        
        this.mockMvc.perform(
                get("/anyRequest")
                        .header("Authorization", "Bearer " + accessToken)
        )
                .andExpect(status().isOk());
    }
}
