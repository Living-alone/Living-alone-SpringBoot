package com.livingalone.springboot.domain.profile.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileTechnic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "technic_name")
    private Technic technic;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
}
