package com.ecommerce.domain;

import com.ecommerce.common.enums.MemberStatus;
import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @NoArgsConstructor(access = AccessLevel.PROTECTED)
 * JPA가 Entity를 생성하기 위해 반드시 필요한 기본 생성자다
 * JPA는 이 생성자를 호출한 뒤 리플렉션으로 필드 값을 주입해 완성한다.
 *
 * 만약 기본 생성자를 public으로 두면 외부 코드에서 의미없는 상태의 엔티티 생성이 가능하다.
 * 이를 막기 위해 접근 제어자를 protected로 설정해 JPA 내부에서만 사용 가능하게 제한한다.
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_MEMBER")
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus memberStatus;

    /**
     * TODO: MemberRole 추가
     */
    /*@OneToMany(mappedBy = "member")
    private List<MemberRole> memberRoles = new ArrayList<>();*/
}