package com.smartmarket.code.util;

import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.CreateTravelInsuranceBICResponse;
import com.smartmarket.code.response.DataCreateBIC;
import com.smartmarket.code.response.ReponseError;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SetResponseUtils {
    //Inquery
    public BaseResponse setResponseInquery(BaseResponse response,
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

        EJson jsonObjectReponseCreate = new EJson(jsonResultPutBIC.getBody());
        Long orderIdCreated = jsonObjectReponseCreate.getLong("orderId");
        boolean succeeded = jsonObjectReponseCreate.getBoolean("succeeded");
        createTravelInsuranceBICResponse.setOrderId(String.valueOf(orderIdCreated));
        createTravelInsuranceBICResponse.setSucceeded(succeeded);
        EJson dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));
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
    public ReponseError setResponseError(ReponseError responseError,
                            BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest,
                            ResponseEntity<String> jsonResultCreateBIC,
                            EJson dataResponse){
        responseError.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        responseError.setResponseTime(DateTimeUtils.getCurrentDate());
        responseError.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        responseError.setResponseId(createTravelInsuranceBICRequest.getRequestId());
        responseError.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
        if (dataResponse != null){
            responseError.setDetailErrorMessage(dataResponse.getString("userMessage"));
        }
        return responseError ;
    }

    //CustomException
    public ReponseError setResponseCustomException(ReponseError response, CustomException ex){
        response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getHttpStatus().toString());
        response.setDetailErrorMessage(ex.getMessage());
        return response ;
    }

    //APIResponseException
    public ReponseError setResponseAPIResponseException(ReponseError response, APIResponseException ex){
        response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getDetailErrorCode().toString());

        response.setDetailErrorMessage(ex.getDetailErrorMessage());
        return response ;
    }

//      APIAccessException
        public ReponseError setResponseAPIAccessException(ReponseError response, APIAccessException ex){
            response.setResultCode(ex.getResultCode());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ex.getResultMessage());
            response.setDetailErrorCode(HttpStatus.REQUEST_TIMEOUT.toString());
            response.setDetailErrorMessage(ex.getDetailErrorMessage());
            return response ;
        }

//      InvalidInputException
        public ReponseError setResponseInvalidInputException(ReponseError response, InvalidInputException ex){
            response.setResultCode(ResponseCode.CODE.INVALID_INPUT_DATA);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.INVALID_INPUT_DATA_MSG);
            response.setResponseId(ex.getRequestId());
            response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
            response.setDetailErrorMessage(ex.getMessage());
            return response ;
        }


    //MethodArgumentNotValidException
    public ReponseError setResponseMethodArgumentNotValidException(ReponseError response, Map<String, String> ex,String requestId){
        response.setResultCode(ResponseCode.CODE.INVALID_INPUT_DATA);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.INVALID_INPUT_DATA_MSG);
        response.setResponseId(requestId);
        response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
        response.setDetailErrorMessage(ex.toString());
        return response ;
    }

//      HttpMessageNotReadableException
        public ReponseError setResponseHttpMessageNotReadableException(ReponseError response, HttpMessageNotReadableException ex){
            response.setResultCode(ResponseCode.CODE.FORMAT_MESSAGE_ERROR);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.FORMAT_MESSAGE_ERROR_MSG);
            response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
            response.setDetailErrorMessage("Format body is not JSON !");
            return response ;
        }

//      Exception
        public ReponseError setResponseException(ReponseError response, Exception ex){
            response.setResultCode(ResponseCode.CODE.GENERAL_ERROR);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.GENERAL_ERROR_MSG);
            response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
            response.setDetailErrorMessage("An error occurred during the processing of the system!");
            return response ;
        }

    //  ConnectDataBaseException
        public ReponseError setResponseConnectDataBaseException(ReponseError response, ConnectDataBaseException ex){
            response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
            response.setDetailErrorCode(HttpStatus.UNPROCESSABLE_ENTITY.toString());
            response.setDetailErrorMessage("An error occurred during the processing of the system!");
            return response ;
        }

//      CustomEntryPoint
        public ReponseError setResponseCustomEntryPoint(ReponseError responseError){
            responseError.setResultCode(ResponseCode.CODE.AUTHORIZED_FAILED);
            responseError.setResponseTime(DateTimeUtils.getCurrentDate());
            responseError.setResultMessage(ResponseCode.MSG.AUTHORIZED_FAILED_MSG);
            responseError.setDetailErrorCode(HttpStatus.UNAUTHORIZED.toString());
            responseError.setDetailErrorMessage("Authorized failed ");
            return responseError ;
        }

}
