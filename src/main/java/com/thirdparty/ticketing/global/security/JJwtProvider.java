package com.thirdparty.ticketing.global.security;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.service.ExpiredTokenException;
import com.thirdparty.ticketing.domain.member.service.InvalidTokenException;
import com.thirdparty.ticketing.domain.member.service.JwtProvider;
import com.thirdparty.ticketing.domain.member.service.response.CustomClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JJwtProvider implements JwtProvider {

    private static final String ROLE = "role";

    private final String issuer;
    private final int expirySeconds;
    private final SecretKey secretKey;
    private final JwtParser accessTokenParser;

    public JJwtProvider(String issuer, int expirySeconds, String secret) {
        this.issuer = issuer;
        this.expirySeconds = expirySeconds;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    @Override
    public CustomClaims parseAccessToken(String accessToken) {
        try {
            Claims payload = accessTokenParser.parseSignedClaims(accessToken).getPayload();
            String email = payload.getSubject();
            MemberRole memberRole = MemberRole.find(payload.get(ROLE, String.class));
            return new CustomClaims(email, memberRole);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("액세스 토큰이 만료되었습니다.");
        } catch (RuntimeException e) {
            log.debug("액세스 토큰이 유효하지 않습니다. token={}", accessToken);
            throw new InvalidTokenException("액세스 토큰이 유효하지 않습니다.", e);
        }
    }

    @Override
    public String createAccessToken(Member member) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirySeconds * 1000L);
        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(now)
                .subject(member.getEmail())
                .expiration(expiresAt)
                .claim(ROLE, member.getMemberRole().getValue())
                .signWith(secretKey)
                .compact();
    }
}
