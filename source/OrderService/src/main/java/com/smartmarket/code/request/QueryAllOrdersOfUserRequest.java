package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class QueryAllOrdersOfUserRequest implements Serializable {

    private int page  ;
    private int size ;
}