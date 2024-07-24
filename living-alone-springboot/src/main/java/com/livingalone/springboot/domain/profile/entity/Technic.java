//package com.livingalone.springboot.domain.profile.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.Set;
//
//@Entity
//@Builder
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Technic {
//    @Id
//    private String technicName;
//    @OneToMany(mappedBy = "technic")
//    private Set<ProfileTechnic> profileTechnics;
//
//    public Technic(String technicName) {
//        this.technicName = technicName;
//    }
//}
