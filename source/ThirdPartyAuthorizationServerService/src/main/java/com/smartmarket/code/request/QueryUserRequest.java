package com.smartmarket.code.request;

import com.smartmarket.code.model.User;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
public class QueryUserRequest implements Serializable {

    private Long page  ;
    private Long size ;
}
