package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateProviderUserRequest {
    @NotNull(message = "ProviderAdminUserCreate is required")
    private ProviderUserCreate user;

    @NotNull(message = "role is required")
    private String role ;
}
