package com.smartmarket.code.util;

import com.smartmarket.code.exception.InvalidInputException;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.UpdateTravelInsuranceBICRequest;
import org.springframework.util.StringUtils;

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


    public static void checkValidUpdate(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest){
        //validate
        if (updateTravelInsuranceBICRequest.getDetail() != null && updateTravelInsuranceBICRequest.getDetail().getOrders() == null) {
            throw new InvalidInputException("order is require", updateTravelInsuranceBICRequest.getRequestId());
        }
        if (updateTravelInsuranceBICRequest.getDetail() != null &&
                updateTravelInsuranceBICRequest.getDetail().getOrders() != null) {
            if (updateTravelInsuranceBICRequest.getDetail().getOrders() .getOrderId()== null) {
                throw new InvalidInputException("orderId is require", updateTravelInsuranceBICRequest.getRequestId());
            }
        }

        if (updateTravelInsuranceBICRequest.getDetail() != null &&
                updateTravelInsuranceBICRequest.getDetail().getTrv() != null) {
            if (updateTravelInsuranceBICRequest.getDetail().getTrv().getDestroy() == null) {
                throw new InvalidInputException("destroy is require", updateTravelInsuranceBICRequest.getRequestId());
            }
            if (updateTravelInsuranceBICRequest.getDetail().getTrv().getTrvId() == null) {
                throw new InvalidInputException("trvId is require", updateTravelInsuranceBICRequest.getRequestId());
            }
            if (updateTravelInsuranceBICRequest.getDetail().getTrv().getOrderId() == null) {
                throw new InvalidInputException("orderId is require", updateTravelInsuranceBICRequest.getRequestId());
            }

        }
    }


    public static void checkValidInquire(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest){

        if (queryTravelInsuranceBICRequest.getDetail() != null ) {
            if (queryTravelInsuranceBICRequest.getDetail().getInquiryType() == null) {
                throw new InvalidInputException("inquiryType is require", queryTravelInsuranceBICRequest.getRequestId());
            }else {
                if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(1L) == true) {
                    if(StringUtils.isEmpty(queryTravelInsuranceBICRequest.getDetail().getOrderId()) )
                    throw new InvalidInputException("orderId is require", queryTravelInsuranceBICRequest.getRequestId());
                }
                if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(2L) == true) {
                    if (StringUtils.isEmpty(queryTravelInsuranceBICRequest.getDetail().getOrderReference())) {
                        throw new InvalidInputException("orderReference is require", queryTravelInsuranceBICRequest.getRequestId());
                    }
                }

            }

        }
    }
}
