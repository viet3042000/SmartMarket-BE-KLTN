package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class QueryOrderRequest implements Serializable {

    //order id in orderService
    @NotBlank(message = "orderReference is require")
    @Size(max = 50, message = "orderEntityId should be less than or equal to 50 characters")
    private String orderEntityId;

    @NotBlank(message = "UserName is require")
    @Size(max = 30, message = "userName should be less than or equal to 30 characters")
    private String userName;

//    @NotNull(message = "inquiryType is required")
//    private Long inquiryType ;
//
//    private String orderId ;
//
//    private String orderReference ;

}
