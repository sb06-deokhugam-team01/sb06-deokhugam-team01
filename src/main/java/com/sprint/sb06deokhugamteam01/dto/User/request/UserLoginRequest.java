package com.sprint.sb06deokhugamteam01.dto.User.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

    private String email;
    private String password;

}
