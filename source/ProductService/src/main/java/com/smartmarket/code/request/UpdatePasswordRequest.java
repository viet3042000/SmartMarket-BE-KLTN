package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class UpdatePasswordRequest implements Serializable {
    @NotBlank(message = "oldPassword is require")
    @Size(max = 30, message = "oldPassword should be less than or equal to 30 characters")
    private String oldPassword;

    @NotBlank(message = "newPassword is require")
    @Size(max = 30, message = "newPassword should be less than or equal to 30 characters")
    private String newPassword;
}
