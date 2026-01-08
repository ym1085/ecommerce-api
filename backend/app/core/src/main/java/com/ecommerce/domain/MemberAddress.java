package com.ecommerce.domain;

import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "TB_MEMBER_ADDRESS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "member_id",
                        "receiver_name",
                        "address",
                        "address_detail" // HINT: 회원명, 주소, 상세 주소가 같은 경우 중복 입력 불가능
                })
        }
)
@Entity
public class MemberAddress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_address_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false)
    private String receiverPhone;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "address_nickname")
    private String addressNickname;

    // 기본 배송지 여부
    @Column(name = "is_default", nullable = false)
    private boolean isDefault;
}
