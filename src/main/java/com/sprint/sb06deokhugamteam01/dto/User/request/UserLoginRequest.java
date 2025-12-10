package com.sprint.sb06deokhugamteam01.dto.User.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
    @Email @NotBlank String email,
    @NotBlank String password
) {

}
