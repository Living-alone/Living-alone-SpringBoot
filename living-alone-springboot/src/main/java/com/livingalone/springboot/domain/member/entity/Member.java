package com.livingalone.springboot.domain.member.entity;

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
@Table(name="user")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id",length = 11,nullable = false)
    private Long id;
    @Column(length = 30, nullable = false)
    private String memberId;
    @Column(length=50, nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition ="TINYINT(1)")
    private int age;
    @Column(nullable = false, length = 15 ,unique = true )
    private String phone_number;
    @Column(nullable = false, length = 100)
    private String school;
    @Column(nullable = false, length = 20,unique = true)
    private String student_id;
    @Column(nullable = false, length = 50,unique = true)
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name="user_status")
    private Status status;
    private String email;


    @ManyToOne(cascade = CascadeType.ALL)
    private Authority authority;
    private Boolean activate;
    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;

    public boolean isActivated() {
        return activate;
    }
}
