package com.thirdparty.ticketing.global.config;

import com.thirdparty.ticketing.domain.member.service.JwtProvider;
import com.thirdparty.ticketing.global.security.AuthenticationContext;
import com.thirdparty.ticketing.global.security.AuthenticationInterceptor;
import com.thirdparty.ticketing.global.security.LoginMemberArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtProvider jwtProvider;
    private final AuthenticationContext authenticationContext;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(jwtProvider, authenticationContext))
                .order(1)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(authenticationContext));
    }
}
