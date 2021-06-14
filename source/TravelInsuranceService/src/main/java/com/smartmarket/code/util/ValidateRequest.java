package com.smartmarket.code.util;

import com.smartmarket.code.exception.InvalidInputException;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;

public class ValidateRequest {

    public static void checkValidCreateOrUpdate(BaseDetail<CreateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest){
        //validate
        if (updateTravelInsuranceBICRequest.getDetail() != null && updateTravelInsuranceBICRequest.getDetail().getOrders() == null) {
            throw new InvalidInputException("Không tìm thấy trường orders trong request", updateTravelInsuranceBICRequest.getRequestId());
        }
        if (updateTravelInsuranceBICRequest.getDetail() != null &&
                updateTravelInsuranceBICRequest.getDetail().getOrders() != null) {
            if (updateTravelInsuranceBICRequest.getDetail().getOrders().getOrderReference() == null) {
                throw new InvalidInputException("Không tìm thấy trường orderReference trong request", updateTravelInsuranceBICRequest.getRequestId());
            }
        }
    }
}
