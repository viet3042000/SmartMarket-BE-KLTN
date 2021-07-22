package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class TRVUpdate {
    private Long trvId ;
    private String orderId;
    private Long destroy;
}
