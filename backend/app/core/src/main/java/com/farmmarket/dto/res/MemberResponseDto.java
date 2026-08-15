package com.farmmarket.dto.res;

import com.farmmarket.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUp {
        private Long id;
        private String email;
        private String name;
        public static SignUp from(Member member) {
            return SignUp.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .build();
        }

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Login {
        private Long memberId;
        private String accessToken;
        private String refreshToken;
        private String tokenType; // "Bearer"
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reissue {
        private String accessToken;
        private String tokenType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Logout {
        private String message;
    }
}
