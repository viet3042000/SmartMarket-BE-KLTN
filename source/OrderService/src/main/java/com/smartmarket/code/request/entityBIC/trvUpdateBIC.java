package com.smartmarket.code.request.entityBIC;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class trvUpdateBIC {
    private Long TRVID;
    private Long Orderid;
    private boolean Destroy;
}
