package com.ecommerce.domain;

import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "TB_ROLE")
public class Role extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    /**
     * TODO: 양방향 연관관계 매핑 필요시 MemberRole mappedBy로 참조
     * 근데 굳이 Role에서 MemberRole을 참조할 필요가 있을지 모르겠음?
     */
    /*@OneToMany(mappedBy = "role")
    private List<MemberRole> memberRoles = new ArrayList<>();*/
}