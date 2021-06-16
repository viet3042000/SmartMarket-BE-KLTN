package com.smartmarket.code.request.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class User {

    @NotNull(message = "abc")
//    @NotBlank(message = "abc")
    private Long userId ;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
