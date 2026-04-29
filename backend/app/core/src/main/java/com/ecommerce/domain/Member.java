package com.ecommerce.domain;

import com.ecommerce.common.enums.MemberStatus;
import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_MEMBER")
@Entity
public class Member extends BaseTimeEntity {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus memberStatus;

    @Column(name = "is_agree_marketing", nullable = false)
    private String isAgreeMarketing;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // 양방향 연관관계 매핑 -> Member(1) : MemberAddress(N)
    @OneToMany(mappedBy = "member")
    private List<MemberAddress> memberAddresses = new ArrayList<>();

    // 양방향 연관관계 매핑 -> Member(1) : MemberRole(N)
    @OneToMany(mappedBy = "member")
    private List<MemberRole> memberRoles = new ArrayList<>();

    // 양방향 연관관계 편의 메서드 -> Member <-> MemberAddress
    public void addMemberAddress(MemberAddress memberAddress) {
        this.memberAddresses.add(memberAddress);
        memberAddress.assignMember(this);
    }

    // 양방향 연관관계 편의 메서드 -> Member <-> MemberRole
    public void addMemberRole(MemberRole memberRole) {
        this.memberRoles.add(memberRole);
        memberRole.assignMember(this);
    }

    @Builder
    public Member(String email,
                  String password,
                  String name,
                  String phoneNumber,
                  MemberStatus memberStatus,
                  String isAgreeMarketing) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.memberStatus = memberStatus;
        this.isAgreeMarketing = isAgreeMarketing;
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