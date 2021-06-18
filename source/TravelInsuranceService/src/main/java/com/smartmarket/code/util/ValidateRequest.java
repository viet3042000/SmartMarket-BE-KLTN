package com.smartmarket.code.util;

import com.smartmarket.code.exception.InvalidInputException;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;

public class ValidateRequest {

    public static void checkValidCreate(BaseDetail<CreateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest){
        //validate
//        if (updateTravelInsuranceBICRequest.getDetail() != null && updateTravelInsuranceBICRequest.getDetail().getOrders() == null) {
//            throw new InvalidInputException("order is require", updateTravelInsuranceBICRequest.getRequestId());
//        }
//        if (updateTravelInsuranceBICRequest.getDetail() != null &&
//                updateTravelInsuranceBICRequest.getDetail().getOrders() != null) {
//            if (updateTravelInsuranceBICRequest.getDetail().getOrders().getOrderReference() == null) {
//                throw new InvalidInputException("orderReference is require", updateTravelInsuranceBICRequest.getRequestId());
//            }
//        }
    }


    public static void checkValidUpdate(BaseDetail<CreateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest){
        //validate
//        if (updateTravelInsuranceBICRequest.getDetail() != null && updateTravelInsuranceBICRequest.getDetail().getOrders() == null) {
//            throw new InvalidInputException("order is require", updateTravelInsuranceBICRequest.getRequestId());
//        }
//        if (updateTravelInsuranceBICRequest.getDetail() != null &&
//                updateTravelInsuranceBICRequest.getDetail().getOrders() != null) {
//            if (updateTravelInsuranceBICRequest.getDetail().getOrders().getOrderReference() == null) {
//                throw new InvalidInputException("orderReference is require", updateTravelInsuranceBICRequest.getRequestId());
//            }
//        }

        if (updateTravelInsuranceBICRequest.getDetail() != null &&
                updateTravelInsuranceBICRequest.getDetail().getTrv() != null) {
            if (updateTravelInsuranceBICRequest.getDetail().getTrv().getStatus() == null) {
                throw new InvalidInputException("status is require", updateTravelInsuranceBICRequest.getRequestId());
            }
            if (updateTravelInsuranceBICRequest.getDetail().getTrv().getTrvId() == null) {
                throw new InvalidInputException("trvId is require", updateTravelInsuranceBICRequest.getRequestId());
            }
            if (updateTravelInsuranceBICRequest.getDetail().getTrv().getOrderId() == null) {
                throw new InvalidInputException("orderId is require", updateTravelInsuranceBICRequest.getRequestId());
            }

        }
    }
}
