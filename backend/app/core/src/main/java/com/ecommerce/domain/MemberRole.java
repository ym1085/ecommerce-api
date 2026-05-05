package com.ecommerce.domain;

import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_MEMBER_ROLE",
    uniqueConstraints = @UniqueConstraint(name = "uk_member_role", columnNames = {
            "member_id",
            "role_id"
    })
)
@Entity
public class MemberRole extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_role_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // 양방향 연관관계 편의 메서드
    protected void assignMember(Member member) {
        this.member = member;
    }

    @Builder
    public MemberRole(Role role) {
        this.role = role;
    }

    public static MemberRole createMemberRole(Role role) {
        return MemberRole.builder()
                .role(role)
                .build();
    }
}
