package com.thirdparty.ticketing.support.controller;

import com.thirdparty.ticketing.domain.common.LoginMember;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/resolver")
public class ResolverTestController {

    @GetMapping
    public String resolve(@LoginMember String email) {
        return email;
    }
}