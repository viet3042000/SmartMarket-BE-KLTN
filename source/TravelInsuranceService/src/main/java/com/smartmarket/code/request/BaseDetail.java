package com.smartmarket.code.request;

import java.io.Serializable;

public class BaseDetail<T> extends BaseRequest implements Serializable {

    private T detail ;

    public T getDetail() {
        return detail;
    }

    public void setDetail(T detail) {
        this.detail = detail;
    }
}
