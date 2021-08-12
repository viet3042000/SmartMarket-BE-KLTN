package com.smartmarket.code.request.entityBIC;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class OrdersUpdateBIC {
    private String OrderReference;
    private Long Orderid;
    private String Ordbillfirstname;
    private String Ordbillmobile;
    private String Ordbillstreet1;
    private String Ordbillemail;
    private Long Ordstatus;
}
