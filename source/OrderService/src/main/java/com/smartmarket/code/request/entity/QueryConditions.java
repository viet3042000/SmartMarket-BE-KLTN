package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class QueryConditions implements Serializable {

    @Size(max = 30, message = "userName should be less than or equal to 30 characters")
    private String userName;

    @Size(max = 30, message = "state should be less than or equal to 100 characters")
    private String state;

}
