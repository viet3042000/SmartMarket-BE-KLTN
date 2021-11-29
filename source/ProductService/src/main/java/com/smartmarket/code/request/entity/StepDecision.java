package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

//Used to ignore null fields in an object.
//If you try to return object,you will find that null fields aren't included in response
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class StepDecision implements Serializable {

    private String requestId;

    private String flowName;

    private Long productId;

//    private String currentStepName;//step1,2,3,4,5
    private int currentStepNumber;//1,2,3,4,5

    private String roleName;

    private String decision;//Approve or DisApprove
}
