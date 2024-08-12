package com.thirdparty.ticketing.domain.member.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMemberRequest {
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "입력값이 이메일 형식을 만족하지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Length(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하여야 합니다.")
    private String password;
}
