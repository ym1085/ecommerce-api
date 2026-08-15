package com.farmmarket.domain;

import com.farmmarket.common.enums.MemberStatus;
import com.farmmarket.common.enums.Role;
import com.farmmarket.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Comment("쇼핑몰 회원 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_MEMBER")
@Entity
public class Member extends BaseTimeEntity {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("회원 PK(예: 1)")
    @Column(name = "member_id")
    private Long id;

    @Comment("회원 이름(예: 홍길동)")
    @Column(name = "name", nullable = false)
    private String name;

    @Comment("암호화된 회원 비밀번호")
    @Column(name = "password", nullable = false)
    private String password;

    @Comment("회원 이메일(예: user@example.com)")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Comment("회원 전화번호(예: 010-1234-5678)")
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Comment("회원 상태(예: ACTIVE, INACTIVE, BLOCKED)")
    @Column(name = "status", nullable = false)
    private MemberStatus memberStatus;

    @Enumerated(EnumType.STRING)
    @Comment("회원 권한(예: USER, ADMIN)")
    @Column(name = "role", nullable = false)
    private Role role;

    @Comment("마케팅 정보 수신 동의 여부(예: Y이면 동의)")
    @Column(name = "is_agree_marketing", nullable = false)
    private String isAgreeMarketing;

    @Comment("최근 로그인일시(예: 2026-08-10 14:30:00)")
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder
    public Member(String email,
                  String password,
                  String name,
                  String phoneNumber,
                  MemberStatus memberStatus,
                  String isAgreeMarketing,
                  Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.memberStatus = memberStatus;
        this.isAgreeMarketing = isAgreeMarketing;
        this.role = role;
    }

    /**
     * 신규 회원 생성 시 호출되는 함수
     * @param email
     * @param password
     * @param name
     * @param phoneNumber
     * @param isAgreeMarketing
     * @return
     */
    public static Member createMember(String email,
                                      String password,
                                      String name,
                                      String phoneNumber,
                                      String isAgreeMarketing) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .phoneNumber(phoneNumber)
                .memberStatus(MemberStatus.ACTIVE) // 가입 시 상태는 ACTIVE("활성화")로 고정
                .isAgreeMarketing(isAgreeMarketing)
                .role(Role.USER) // 가입 시 권한은 USER로 고정
                .build();
    }

    /**
     * 회원 비밀번호 변경
     * @param password
     */
    public void updatePassword(String password) {
        this.password = password;
    }

    /**
     * 회원 계정 상태 변경
     * @param status ACTIVE, INACTIVE, DELETED, BLOCKED
     */
    public void updateStatus(MemberStatus status) {
        this.memberStatus = status;
    }

    /**
     * 회원 프로필(Profile) 정보 변경
     * @param name
     * @param email
     * @param phoneNumber
     */
    public void updateProfile(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * 최근 로그인일자 변경
     */
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
