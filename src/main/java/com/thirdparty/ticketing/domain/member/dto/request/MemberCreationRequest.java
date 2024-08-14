package com.thirdparty.ticketing.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreationRequest {
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "입력값이 이메일 형식을 만족하지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Length(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하여야 합니다.")
    private String password;
}
