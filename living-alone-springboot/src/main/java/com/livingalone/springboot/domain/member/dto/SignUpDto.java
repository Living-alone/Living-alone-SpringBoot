package com.livingalone.springboot.domain.member.dto;

import com.livingalone.springboot.domain.member.entity.Gender;
import com.livingalone.springboot.domain.member.entity.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {
    private String memberId;
    private String name;
    private String password;
    private int age;
    private String phoneNumber;
    private String school;
    private String studentId;
    private String nickname;
    private Gender gender;

    @Builder.Default
    private Status status = Status.ACTIVE;
}
