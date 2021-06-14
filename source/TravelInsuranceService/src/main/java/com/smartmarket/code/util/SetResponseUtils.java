package com.smartmarket.code.util;

import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.CreateTravelInsuranceBICResponse;
import com.smartmarket.code.response.DataCreateBIC;
import com.smartmarket.code.response.ReponseError;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SetResponseUtils {
    //Inquery
    public BaseResponse setResponse(BaseResponse response,
                            CreateTravelInsuranceBICRequest createTravelInsuranceBICResponse){
        response.setDetail(createTravelInsuranceBICResponse);
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
        return response ;
    }

    //Create/Update
    public BaseResponse setResponse(BaseResponse response,
                            BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest,
                            CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse,
                            ResponseEntity<String> jsonResultPutBIC){

        JSONObject jsonObjectReponseCreate = new JSONObject(jsonResultPutBIC.getBody());
        Long orderIdCreated = jsonObjectReponseCreate.getLong("Orderid");
        boolean succeeded = jsonObjectReponseCreate.getBoolean("succeeded");
        createTravelInsuranceBICResponse.setOrderId(String.valueOf(orderIdCreated));
        createTravelInsuranceBICResponse.setSucceeded(succeeded);
        JSONObject dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));
        DataCreateBIC dataCreateBIC = new DataCreateBIC();
        dataCreateBIC.setMessage(dataResponse.getString("userMessage"));
        dataCreateBIC.setCreatedate(dataResponse.getString("internalMessage"));
        createTravelInsuranceBICResponse.setData(dataCreateBIC);
        response.setDetail(createTravelInsuranceBICResponse);
        response.setResponseId(createTravelInsuranceBICRequest.getRequestId());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
        response.setResponseTime(dataResponse.getString("internalMessage"));
        return response ;
    }

    //Create Error
    public ReponseError setResponse(ReponseError responseError,
                            BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest,
                            ResponseEntity<String> jsonResultCreateBIC,
                            JSONObject dataResponse){
        responseError.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        responseError.setResponseTime(DateTimeUtils.getCurrentDate());
        responseError.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        responseError.setResponseId(createTravelInsuranceBICRequest.getRequestId());
        responseError.setDetailErrorCode(jsonResultCreateBIC.getStatusCode().toString());
        responseError.setDetailErrorMessage(dataResponse.getString("userMessage"));
        return responseError ;
    }
}
