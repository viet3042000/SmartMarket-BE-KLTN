package com.smartmarket.code.response;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Response {
    private int code = 1;
    private Object data;

	public Response() {

    }

}
