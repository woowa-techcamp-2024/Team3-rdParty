package com.thirdparty.ticketing.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.controller.request.CreateMemberRequest;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.member.service.response.CreateMemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberServiceTest {

    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(String rawPassword) {
                return new StringBuilder(rawPassword).reverse().toString();
            }

            @Override
            public void checkMatches(Member member, String rawPassword) {
                // 사용되지 않음
            }
        };
        memberService = new MemberService(memberRepository, passwordEncoder);
    }

    @Nested
    @DisplayName("회원 생성 메서드 호출 시")
    class CreateMemberTest {

        @Test
        @DisplayName("회원을 생성한다.")
        void createMember() {
            //given
            String email = "email@email.com";
            String password = "password";
            CreateMemberRequest request = new CreateMemberRequest(email, password);

            //when
            CreateMemberResponse response = memberService.createMember(request);

            //then
            assertThat(memberRepository.findById(response.getMemberId()))
                    .isNotEmpty()
                    .get()
                    .satisfies(member -> {
                        assertThat(member.getEmail()).isEqualTo(email);
                        assertThat(member.getPassword()).isEqualTo(passwordEncoder.encode(password));
                        assertThat(member.getMemberRole()).isEqualTo(MemberRole.USER);
                    });
        }

        @Test
        @DisplayName("예외(duplicateResource): 중복된 이메일을 가진 회원이 있으면")
        void duplicateResource_WhenDuplicateEmail() {
            //given
            String duplicateEmail = "duplicate@email.com";
            String password = "password";
            Member member = Member.builder()
                    .email(duplicateEmail)
                    .password(password)
                    .memberRole(MemberRole.USER)
                    .build();
            memberRepository.save(member);

            CreateMemberRequest request = new CreateMemberRequest(duplicateEmail, password);

            //when
            Exception exception = catchException(() -> memberService.createMember(request));

            //then
            assertThat(exception).isInstanceOf(DuplicateResourceException.class);
        }
    }
}
