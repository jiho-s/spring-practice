package me.jiho.oauthdemo.domain.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jiho.oauthdemo.domain.common.BaseEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseEntity {

    private String name;

    private String email;

    private String password;

    @ElementCollection
    @Enumerated(value = EnumType.ORDINAL)
    Set<MemberRole> role;

    @Builder
    public Member(String name, String email, String password,Set<MemberRole> role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
