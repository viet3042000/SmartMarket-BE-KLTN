package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class QueryRoleRequest implements Serializable {

    private Long page  ;
    private Long size ;
}
