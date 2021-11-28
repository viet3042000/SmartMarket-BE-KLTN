package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface ApprovalFlowService {
    public void createApprovalFlow(Map<String, Object> keyPairs) throws ParseException;
    public void updateApprovalFlow(Map<String, Object> keyPairs) throws ParseException;
    public void deleteApprovalFlow(Map<String, Object> keyPairs);
    public void truncateApprovalFlow();
}
