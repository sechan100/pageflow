package org.pageflow.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.SignupCache;
import org.pageflow.domain.user.model.dto.SignupForm;
import org.pageflow.domain.user.model.dto.WebLoginRequest;
import org.pageflow.domain.user.repository.SignupCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : sechan
 */
@SpringBootTest
@AutoConfigureMockMvc
class SignupLoginTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private SignupCacheRepository signupCacheRepository;
    
    // NATIVE로 회원가입
    @Test
    public void nativeSignup() throws Exception {
        
        String username = "pagepage";
        String password = "aC0~~v#QRmw";
        String passwordConfirm = "aC0~~v#QRmw";
        String email = "pagefloworg@gmail.com";
        String penname = "페이징";
        String profileImgUrl = null;
        
        
        String body = String.format(
                """
                        {
                          "username": "%s",
                          "password": "%s",
                          "passwordConfirm": "%s",
                          "email": "%s",
                          "penname": "%s"
                        }
                        """,
                username, password, passwordConfirm, email, penname
        );
        
        
        MockHttpServletRequestBuilder request = post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
        

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider", is("NATIVE")))
                .andExpect(jsonPath("$.emailVerified", is(false)))
                .andExpect(jsonPath("$.role", is(RoleType.ROLE_USER.toString())))
                .andExpect(jsonPath("$.penname", is(penname)));
    }
    
    // NATIVE 회원가입 후 로그인
    @Test
    public void nativeLogin() throws Exception {
        
        nativeSignup();
        
        String username = "pagepage";
        String password = "aC0~~v#QRmw";
        
        this.mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((new ObjectMapper()).writeValueAsString(
                                WebLoginRequest.builder()
                                        .username(username)
                                        .password(password)
                                        .build()
                        ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }
    
    // OAuth2(GOOGLE)로 회원가입
    @Test
    public void oauth2Signup() throws Exception {
        
        String username = "googleNewUser";
        String profileImgUrl = "https://pageflow.org/profileImgUrl";
        
        // OAuth2 요청 했다고치고, 캐싱된 데이터가 존재하는 것처럼 Mocking
        Mockito.when(signupCacheRepository.findById(username))
                .thenReturn(Optional.of(
                        SignupCache.builder()
                                .username(username)
                                .email("pagefloworg@gmail.com")
                                .provider(ProviderType.GOOGLE)
                                .penname("멍청이")
                                .profileImgUrl(profileImgUrl)
                                .build()
                )
        );
        Mockito.when(signupCacheRepository.existsByUsername(username))
                .thenReturn(true);
        
        
        String radomPassword = UUID.randomUUID().toString();
        String body = (new ObjectMapper()).writeValueAsString(
                SignupForm.builder()
                        .username(username)
                        .password(radomPassword)
                        .passwordConfirm(radomPassword)
                        .email("pagefloworg@gmail.com")
                        .penname("펜네임이랑께")
                        .profileImgUrl(profileImgUrl)
                        .build()
        );
        
        MockHttpServletRequestBuilder request = post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
        
        
        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider", is("GOOGLE")))
                .andExpect(jsonPath("$.emailVerified", is(false)))
                .andExpect(jsonPath("$.role", is(RoleType.ROLE_USER.toString())))
                .andExpect(jsonPath("$.penname", is("펜네임이랑께")));
    }
    
    
    
    
    
}
