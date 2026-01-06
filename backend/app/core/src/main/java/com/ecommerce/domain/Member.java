package com.ecommerce.domain;

import com.ecommerce.common.enums.MemberStatus;
import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "TB_MEMBER")
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