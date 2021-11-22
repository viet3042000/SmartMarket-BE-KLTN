package com.smartmarket.code.service;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface TravelInsuranceOutboxService {
    public void processMessageFromTravelOutbox(JSONObject jsonPayload, String aggregateId, String type) throws Exception;
}
