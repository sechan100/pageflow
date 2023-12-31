package org.pageflow.domain.user.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.TokenSession;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.TokenSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;


@Service
@Transactional
@Slf4j
public class JwtProvider {
    
    @Value("${jwt.secret}")
    private String secretKey;
    private final TokenSessionRepository tokenSessionRepository;
    private final AccountRepository accountRepository;
    private Key signKey;
    private static final String USER_ID_KEY = "UID";
    private static final String AUTHORITY_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRED_IN = (1000 * 60) * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRED_IN = (1000 * 60) * 60 * 24 * 14; // 14일
    
    public JwtProvider(
            TokenSessionRepository tokenSessionRepository,
            AccountRepository accountRepository
    ) {
        this.tokenSessionRepository = tokenSessionRepository;
        this.accountRepository = accountRepository;
    }
    
    public TokenDto generateTokenDto(Long userId, Authentication authentication) {
        
        // [[ Access Token 생성
        TokenResult accessTokenRes = generateAccessToken(
                userId,
                authentication.getName(),
                RoleType.getRoleType(authentication.getAuthorities())
        );
        
        String accessToken = accessTokenRes.getToken();
        long accessTokenExpiredIn = accessTokenRes.getExpiredIn();
        // ]]
        
        // [[ Refresh Token 생성
        String refreshToken = generateRefreshToken(userId, authentication.getName()).getToken();
        // ]]
        
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredIn(accessTokenExpiredIn)
                .build();
    }
    
    @Data
    @Builder
    private static class TokenResult {
        private String token;
        private long expiredIn;
    }
    
    /**
     * access token 발급
     */
    public TokenResult generateAccessToken(Long userId, String username, RoleType roleType) {
        
        Date expiredIn = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_IN);
        
        String token = Jwts.builder()
                .setSubject(username) // "sub": "username"
                .addClaims(Map.of(USER_ID_KEY, userId)) // "UID": 34
                .addClaims(Map.of(AUTHORITY_KEY, roleType)) // "auth": "ROLE_USER"
                .setIssuedAt(new Date()) // "iat": 1516239022
                .setExpiration(expiredIn) // "exp": 1516239022
                .signWith(getSignKey())
                .compact();
        
        return TokenResult.builder()
                .token(token)
                .expiredIn(expiredIn.getTime())
                .build();
    }

    /**
     *  refresh token 발급 및 저장
     */
    public TokenResult generateRefreshToken(Long userId, String username) {
        
        Date expiredIn = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRED_IN);
        
        // Token Session에 사용할 Id 값인 UUID를 생성
        String sessionId = UUID.randomUUID().toString();
        
        // refresh token 생성
        String token = Jwts.builder()
                .setSubject(sessionId) // "subject": "8a5ff68f-e9f8-49da-899a-8222aedc69b8"
                .addClaims(Map.of(USER_ID_KEY, userId)) // "UID": 34
                .setIssuedAt(new Date()) // "iat": 1516239022
                .setExpiration(expiredIn) // "exp": 1516239022
                .signWith(getSignKey())
                .compact();
        
        // TokenSession에 refreshToken을 저장(유니크 키: username_expiredIn(UTC))
        String sessionKey = username + "_" +  expiredIn.getTime();
        
        
        try {
            // 저장
            tokenSessionRepository.save(
                    TokenSession.builder()
                            .id(sessionId) // UUID
                            .refreshToken(token) // refresh token
                            .account(accountRepository.getReferenceById(userId)) // account
                            .build()
            );
        } catch(Exception e) {
            log.error("refresh token 영속화 실패: {}", e.getMessage());
        }
        
        
        return TokenResult.builder()
                .token(token)
                .expiredIn(expiredIn.getTime())
                .build();
    }
    
    /**
     * access token을 통해 인증객체를 추출
     * @throws IllegalArgumentException 토큰에 권한 정보가 없는 경우.(refresh token인 경우)
     */
    public Authentication parseAccessToken(String accessToken) {
        
        // 토큰 복호화
        Claims claims = parseToken(accessToken);
        
        // 권한 추출
        Optional<String> authorityOrNull = Optional.ofNullable(claims.get(AUTHORITY_KEY).toString());
        
        // 권한 정보 유무 확인
        if (authorityOrNull.isEmpty()) {
            throw new IllegalArgumentException("권한 정보가 없는 토큰입니다.");
        }
        
        
        // principal 작성
        UserDetails principal = new PrincipalContext(
                claims.get(USER_ID_KEY, Long.class),
                claims.getSubject(),
                "",
                RoleType.valueOf(authorityOrNull.get())
        );
        
        // 인증 객체 작성 및 반환
        return new UsernamePasswordAuthenticationToken(
                principal,
                "",
                RoleType.getAuthorities(authorityOrNull.get())
        );
    }
    
    public void removeRefreshToken(String refreshToken) {
        
        Claims claims;
        try {
            // 토큰 복호화
            claims = parseToken(refreshToken);
        } catch(ExpiredJwtException e) {
            // 만료된 토큰이라도, 어차피 토큰을 삭제하기 위함이므로, 그냥 해석해서 삭제한다.
            claims = decodeExpiredJwtToken(refreshToken);
        }
        
        // 토큰 세션을 삭제
        String sessionId = claims.getSubject(); // UUID
        tokenSessionRepository.deleteById(sessionId);
    }
    
    
    
    
    public Key getSignKey() {
        if(signKey == null){
            byte[] decodedKey = secretKey.getBytes();
            signKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
        }
        return signKey;
    }
    
    /**
     * @param token 토큰
     * @return 클레임
     * @throws ExpiredJwtException 토큰이 만료된 경우
     * @throws JwtException 그 외의 파싱 예외
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
            
        // 만료예외는 호출한 곳에서 처리
        } catch(ExpiredJwtException e) {
            throw e;
        // 그 외의 예외는 JwtException으로 던짐
        } catch(Exception exception) {
            log.error("토큰 해석 실패: {} ", exception.getMessage());
            throw new JwtException(exception.getMessage(), exception);
        }
    }
    
    /**
     * 일반적인 파싱으로는 ExpiredJwtException이 발생하는 만료된 토큰을 억지로 디코딩
     * @param jwtToken jwt 토큰
     * @return 만료된 토큰의 클레임
     */
    private Claims decodeExpiredJwtToken(String jwtToken) {
        String payload = jwtToken.split("\\.")[1]; // header.payload.signature 구조를 스플릿
        String decodedJson = new String(Base64.getDecoder().decode(payload));
        // JSON을 Claims 객체로 변환
        return Jwts.parserBuilder().build().parseClaimsJws(decodedJson).getBody();
    }
}
