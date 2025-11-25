package com.sprint.sb06deokhugamteam01.dto.User.request;

public record UserRegisterRequest(
    String email,
    String password,
    String nickname
) {

}
