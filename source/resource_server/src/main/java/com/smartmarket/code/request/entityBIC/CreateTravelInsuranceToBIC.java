package com.smartmarket.code.request.entityBIC;

import java.util.List;

public class CreateTravelInsuranceToBIC {

    private OrdersBIC Orders ;
    private trvBIC TRV ;
    private List<trvDetailBIC> TRVDetail;

    public OrdersBIC getOrders() {
        return Orders;
    }

    public void setOrders(OrdersBIC orders) {
        Orders = orders;
    }

    public trvBIC getTRV() {
        return TRV;
    }

    public void setTRV(trvBIC TRV) {
        this.TRV = TRV;
    }

    public List<trvDetailBIC> getTRVDetail() {
        return TRVDetail;
    }

    public void setTRVDetail(List<trvDetailBIC> TRVDetail) {
        this.TRVDetail = TRVDetail;
    }
}
