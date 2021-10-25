package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class ForgotPasswordRequest implements Serializable {
    @NotBlank(message = "email is require")
    @Size(max =50, message = "email should be less than or equal to 50 characters")
    @Email
    private String email;
}
