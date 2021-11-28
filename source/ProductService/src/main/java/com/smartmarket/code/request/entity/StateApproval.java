package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

//Used to ignore null fields in an object.
//If you try to return object,you will find that null fields aren't included in response
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class StateApproval implements Serializable {
    private String stateName;
    private String roleName;
}
