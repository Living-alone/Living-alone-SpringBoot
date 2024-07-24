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
//    private String memberId;
//    @NotEmpty
//    @Size(max = 30)
//    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
//    private String email;
//    @NotEmpty
//    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
//    private String password;
//    /*@NotEmpty
//    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
//    private String checkPassWord;*/
//    @NotEmpty
//    @Size(max = 10)
//    private String name;
     String memberId;
    private String name;
    private String password;
    private int age;
    private String phoneNumber;
    private String school;
    private String studentId;
    private String nickname;
    private Gender gender;
    private Status status;
}
