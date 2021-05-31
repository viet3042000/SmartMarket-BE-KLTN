package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.Orders;
import com.smartmarket.code.request.entity.TRV;
import com.smartmarket.code.request.entity.TRVDetail;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateTravelInsuranceBICRequest implements Serializable {

   private Orders orders ;
   private TRV trv ;
   private ArrayList<TRVDetail> trvDetails ;

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public TRV getTrv() {
        return trv;
    }

    public void setTrv(TRV trv) {
        this.trv = trv;
    }

    public ArrayList<TRVDetail> getTrvDetails() {
        return trvDetails;
    }

    public void setTrvDetails(ArrayList<TRVDetail> trvDetails) {
        this.trvDetails = trvDetails;
    }
}
