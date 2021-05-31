package com.smartmarket.code.response;

public class CreateTravelInsuranceBICResponse {
    private boolean succeeded;
    private String orderId ;
    private DataCreateBIC data ;

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public DataCreateBIC getData() {
        return data;
    }

    public void setData(DataCreateBIC data) {
        this.data = data;
    }
}
