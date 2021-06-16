package com.smartmarket.code.response;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DataCreateBIC implements Serializable {
    private String message ;
    private String createdate ;
    private final static String type = "CREATE";

}
