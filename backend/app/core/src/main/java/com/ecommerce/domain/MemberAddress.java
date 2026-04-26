package com.ecommerce.domain;

import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_MEMBER_ADDRESS")
@Entity
public class MemberAddress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_address_id")
    private Long id;

    @Column(name = "address_nickname", nullable = false)
    private String addressNickname;

    @Column(name = "is_default", nullable = false)
    private String isDefault;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 양방향 연관관계 편의 메서드
    public void setMember(Member member) {
        this.member = member;
    }

    @Builder
    public MemberAddress(String addressNickname,
                         String isDefault,
                         String zipCode,
                         String address,
                         String addressDetail) {
        this.addressNickname = addressNickname;
        this.isDefault = isDefault;
        this.zipCode = zipCode;
        this.address = address;
        this.addressDetail = addressDetail;
    }

    public static MemberAddress createMemberAddress(String addressNickname,
                                                    String isDefault,
                                                    String zipCode,
                                                    String address,
                                                    String addressDetail) {
        return MemberAddress.builder()
                .addressNickname(addressNickname)
                .isDefault(isDefault)
                .zipCode(zipCode)
                .address(address)
                .addressDetail(addressDetail)
                .build();
    }

    /**
     * 기본 배송지 변경
     * @param isDefault
     */
    public void updateIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}
