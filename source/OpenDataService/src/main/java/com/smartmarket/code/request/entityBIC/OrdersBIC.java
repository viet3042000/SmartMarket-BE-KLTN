package com.smartmarket.code.request.entityBIC;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class OrdersBIC {

    private String OrderReference;
    private Long Orderid;
    private Long Ordcustid;
    private String Ordcustmessage;
    private String Ordbillfirstname;
    private String Ordbillmobile;
    private String Ordbillstreet1;
    private String Ordbillemail;
    private String Orddate;
    private Long Ordstatus;
    private String ProductId;
    private Long Ordtotalqty;
    private Long Orderpaymentmethod;
    private String Ordershipmodule;
    private Long Ordisdigital;
    private String Ordtoken;
    private BigDecimal Ordpaidmoney;
    private BigDecimal Ordtotal;
    private BigDecimal Orddiscountamount;
    private Long UserId ;
    private String Ordsource ;

}
