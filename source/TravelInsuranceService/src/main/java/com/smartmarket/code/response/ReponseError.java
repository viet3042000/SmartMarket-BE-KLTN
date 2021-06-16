package com.smartmarket.code.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ReponseError {

    private String responseId;
    private String responseTime;
    private String resultCode;
    private String resultMessage;
    private String detailErrorCode;
    private String detailErrorMessage;

}
