package com.farmmarket.domain;

import com.farmmarket.common.enums.SocialProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Comment("회원 소셜 로그인 연동 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_MEMBER_SOCIAL")
@Entity
public class MemberSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("소셜 로그인 연동 PK(예: 10)")
    @Column(name = "social_id")
    private Long id;

    @Comment("소셜 계정을 연동한 회원 PK(예: 1)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Comment("소셜 로그인 제공자(예: KAKAO, NAVER, GOOGLE)")
    @Column(name = "provider", nullable = false)
    private SocialProvider provider;

    @Comment("소셜 제공자가 발급한 회원 식별자(예: 1234567890)")
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Builder
    public MemberSocial(Member member, SocialProvider provider, String providerId) {
        this.member = member;
        this.provider = provider;
        this.providerId = providerId;
    }

    /**
     * 신규 소셜 연동 정보 생성 시 호출되는 함수
     * (member 연결은 Member.addMemberSocial() 에서 처리)
     */
    public static MemberSocial createMemberSocial(Member member, SocialProvider provider, String providerId) {
        return MemberSocial.builder()
                .member(member)
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
