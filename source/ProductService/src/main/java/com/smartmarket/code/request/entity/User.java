package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class User {

    private Long id;

    private String username;

    private String password;

    private Long enabled;

}
