package com.smartmarket.code.service;

import org.json.JSONObject;
import java.text.ParseException;

public interface TravelInsuranceOutboxService {
    public void processMessageFromTravelOutbox(JSONObject j, String requestId, String status,
                                               String aggregateId, String type) throws ParseException;
}
