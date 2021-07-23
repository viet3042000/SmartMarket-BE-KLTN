package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class TRVUpdate {
    private Long trvId ;
    private String orderId;

    @Range(min= 0, max= 1)
    private Long destroy;
}
