package com.ecommerce.domain;

import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Comment("회원 배송지 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_MEMBER_ADDRESS")
@Entity
public class MemberAddress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("회원 배송지 PK(예: 10)")
    @Column(name = "member_address_id")
    private Long id;

    @Comment("배송지 이름(예: 우리 집, 회사)")
    @Column(name = "address_nickname", nullable = false)
    private String addressNickname;

    @Comment("기본 배송지 여부(예: Y이면 기본 배송지)")
    @Column(name = "is_default", nullable = false)
    private String isDefault;

    @Comment("배송지 우편번호(예: 06236)")
    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Comment("배송지 기본 주소(예: 서울특별시 강남구 테헤란로 123)")
    @Column(name = "address", nullable = false)
    private String address;

    @Comment("배송지 상세 주소(예: 101동 1001호)")
    @Column(name = "address_detail")
    private String addressDetail;

    @Comment("배송지를 등록한 회원 PK(예: 1)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

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

    /**
     * 주소 정보 변경
     * @param addressNickname
     * @param zipCode
     * @param address
     * @param addressDetail
     */
    public void updateAddress(String addressNickname,
                              String zipCode,
                              String address,
                              String addressDetail) {
        this.addressNickname = addressNickname;
        this.zipCode = zipCode;
        this.address = address;
        this.addressDetail = addressDetail;
    }
}
