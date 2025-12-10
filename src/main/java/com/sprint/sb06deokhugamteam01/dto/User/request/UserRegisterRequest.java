package com.sprint.sb06deokhugamteam01.dto.User.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
    @Email @NotBlank String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank @Size(min = 2, max = 20) String nickname
) {

}
