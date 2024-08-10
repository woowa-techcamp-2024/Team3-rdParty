package com.thirdparty.ticketing.domain.member;

import com.thirdparty.ticketing.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    public Member(String email, String password, MemberRole memberRole, ZonedDateTime createdAt) {
        super(createdAt);
        this.email = email;
        this.password = password;
        this.memberRole = memberRole;
    }
}
