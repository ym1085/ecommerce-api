package com.ecommerce.domain;

import com.ecommerce.common.enums.SocialProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_MEMBER_SOCIAL")
@Entity
public class MemberSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private SocialProvider provider;

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
