package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.UserCreate;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
public class CreateUserRequest implements Serializable {

    private UserCreate user;
    private ArrayList<Long> roles ;

}
