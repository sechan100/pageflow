package org.pageflow.core.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.core.PageflowApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : sechan
 */
@SpringBootTest(classes = PageflowApplication.class)
@AutoConfigureMockMvc
class SignupUseCaseTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("/signup")
  void signup() throws Exception {
    String body = """
      {
        "username": "user1",
        "password": "user1",
        "email": "user1@gmail.com",
        "penname": "user1"
      }
    """;

    var result = mockMvc.perform(post("/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isOk())
      .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseBody));
  }
}