package com.smartmarket.code.util;

import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateOrderRequest;

public class ValidateRequest {

    public static void checkValidCreate(BaseDetail<CreateOrderRequest> updateTravelInsuranceBICRequest){
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


//    public static void checkValidUpdate(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest){
//        //validate
//        if (updateTravelInsuranceBICRequest.getDetail() != null && updateTravelInsuranceBICRequest.getDetail().getOrders() == null) {
//            throw new InvalidInputException("order is require", updateTravelInsuranceBICRequest.getRequestId());
//        }
//        if (updateTravelInsuranceBICRequest.getDetail() != null &&
//                updateTravelInsuranceBICRequest.getDetail().getOrders() != null) {
//            if (updateTravelInsuranceBICRequest.getDetail().getOrders() .getOrderId()== null) {
//                throw new InvalidInputException("orderId is require", updateTravelInsuranceBICRequest.getRequestId());
//            }
//        }
//
//        if (updateTravelInsuranceBICRequest.getDetail() != null &&
//                updateTravelInsuranceBICRequest.getDetail().getTrv() != null) {
//            if (updateTravelInsuranceBICRequest.getDetail().getTrv().getDestroy() == null) {
//                throw new InvalidInputException("destroy is require", updateTravelInsuranceBICRequest.getRequestId());
//            }
//            if (updateTravelInsuranceBICRequest.getDetail().getTrv().getTrvId() == null) {
//                throw new InvalidInputException("trvId is require", updateTravelInsuranceBICRequest.getRequestId());
//            }
//            if (updateTravelInsuranceBICRequest.getDetail().getTrv().getOrderId() == null) {
//                throw new InvalidInputException("orderId is require", updateTravelInsuranceBICRequest.getRequestId());
//            }
//
//        }
//    }

}
