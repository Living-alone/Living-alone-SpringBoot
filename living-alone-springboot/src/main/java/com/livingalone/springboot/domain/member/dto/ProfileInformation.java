package com.livingalone.springboot.domain.member.dto;


//import capstone.capstone.domain.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileInformation {
    private String name;
    private String introduce;
    private String job;
    private String specificDuty;
    private Set<String> technics;
    private String link;
    private Integer likeCount;
//    private List<Board> boards;
    private String imageUrl;
    private String openChatting;
}
