package com.smartmarket.code.response;


import com.smartmarket.code.constants.ResponseCode;

public class Response {
    private int code = ResponseCode.UNKNOWN_ERROR;
    private Object data;
	
	public Response() {

    }


	public Response(int code, Object dataResponse) {
        this.code = code;
        this.data = dataResponse;
    }

    public Response(int code) {
        super();
        this.code = code;
    }

    public Response(Object data) {
        super();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return ResponseCode.getMessage(code);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "code: " + code;
    }

}
