package com.smartmarket.code.util;

import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.APIResponseException;
import com.smartmarket.code.exception.APITimeOutRequestException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.exception.InvalidInputException;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.CreateTravelInsuranceBICResponse;
import com.smartmarket.code.response.DataCreateBIC;
import com.smartmarket.code.response.ReponseError;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    //CustomException
    public ReponseError setResponse(ReponseError response, CustomException ex){
        response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getHttpStatus().toString());
        response.setDetailErrorMessage(ex.getMessage());
        return response ;
    }

    //APIResponseException
    public ReponseError setResponse(ReponseError response, APIResponseException ex){
        response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getDetailErrorCode());
        response.setDetailErrorMessage(ex.getDetailErrorMessage());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        return response ;
    }

//      APITimeOutRequestException
        public ReponseError setResponse(ReponseError response, APITimeOutRequestException ex){
            response.setResultCode(ResponseCode.CODE.SOA_TIMEOUT_BACKEND);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG);
            response.setDetailErrorCode(HttpStatus.REQUEST_TIMEOUT.toString());
            response.setDetailErrorMessage(ex.getDetailErrorMessage());
            return response ;
        }

//      InvalidInputException
        public ReponseError setResponse(ReponseError response, InvalidInputException ex){
            response.setResultCode(ResponseCode.CODE.INVALID_INPUT_DATA);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.INVALID_INPUT_DATA_MSG);
            response.setResponseId(ex.getRequestId());
            response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
            response.setDetailErrorMessage(ex.getMessage());
            return response ;
        }

//      HttpMessageNotReadableException
        public ReponseError setResponse(ReponseError response, HttpMessageNotReadableException ex){
            response.setResultCode(ResponseCode.CODE.INVALID_INPUT_DATA);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.INVALID_INPUT_DATA_MSG);
            response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
            response.setDetailErrorMessage("Body request sai format json!");
            return response ;
        }

//      Exception
        public ReponseError setResponse(ReponseError response, Exception ex){
            response.setResultCode(ResponseCode.CODE.FORMAT_MESSAGE_ERROR);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.FORMAT_MESSAGE_ERROR_MSG);
            response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
            response.setDetailErrorMessage("Lỗi xảy ra trong quá trình xử lý của hệ thống ");
            return response ;
        }

//      CustomEntryPoint
        public ReponseError setResponse(ReponseError responseError){
            responseError.setResultCode(ResponseCode.CODE.AUTHORIZED_FAILED);
            responseError.setResponseTime(DateTimeUtils.getCurrentDate());
            responseError.setResultMessage(ResponseCode.MSG.AUTHORIZED_FAILED_MSG);
            responseError.setDetailErrorCode(HttpStatus.UNAUTHORIZED.toString());
            responseError.setDetailErrorMessage("Authorized failed ");
            return responseError ;
        }

}
