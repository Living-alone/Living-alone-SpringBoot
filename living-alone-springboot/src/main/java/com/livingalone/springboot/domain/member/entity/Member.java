package com.livingalone.springboot.domain.member.entity;

//import capstone.capstone.domain.profile.entity.Profile;
//import capstone.capstone.global.jwt.entity.Authority;
import com.livingalone.springboot.domain.profile.entity.Profile;
import com.livingalone.springboot.global.jwt.entity.Authority;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String memberId;
    private String password;

    @ManyToOne(cascade = CascadeType.ALL)
    private Authority authority;
    private Boolean activate;
    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;

    public boolean isActivated() {
        return activate;
    }
}
