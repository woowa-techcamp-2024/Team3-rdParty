package com.thirdparty.ticketing.jpa.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thirdparty.ticketing.jpa.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}
