package com.popflix.domain.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetCreditsResponseDto {
    private List<CastMemberDto> cast;
    private List<CrewMemberDto> crew;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CastMemberDto {
        private String name;
        private String character;
        private Integer gender;
        private String creditId;

        @Override
        public String toString() {
            return "CastMemberDto{" +
                    "name='" + name + '\'' +
                    ", character='" + character + '\'' +
                    ", gender=" + gender +
                    ", creditId='" + creditId + '\'' +
                    '}';
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrewMemberDto {
        private String department;
        private String job;
        private String name;
        private Integer gender;
        private String creditId;

        @Override
        public String toString() {
            return "CrewMemberDto{" +
                    "department='" + department + '\'' +
                    ", job='" + job + '\'' +
                    ", name='" + name + '\'' +
                    ", gender=" + gender +
                    ", creditId='" + creditId + '\'' +
                    '}';
        }
    }
}
