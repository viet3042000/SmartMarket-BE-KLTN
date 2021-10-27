package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank(message = "newPassword is require")
    @Size(max = 30, message = "newPassword should be less than or equal to 30 characters")
    private String newPassword;

    private String token;
}
