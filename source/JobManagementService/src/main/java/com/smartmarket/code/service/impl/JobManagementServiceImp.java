package com.smartmarket.code.service.impl;

import com.google.common.base.Throwables;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.IntervalHistoryRepository;
import com.smartmarket.code.dao.JobHistoryRepository;
import com.smartmarket.code.dao.JobManagementOutboxRepository;
import com.smartmarket.code.dao.PendingBICTransactionRepository;
import com.smartmarket.code.model.IntervalHistory;
import com.smartmarket.code.model.JobHistory;
import com.smartmarket.code.model.JobManagementOutbox;
import com.smartmarket.code.model.PendingBICTransaction;
import com.smartmarket.code.model.entitylog.JobManagementExceptionObject;
import com.smartmarket.code.model.entitylog.ListenerExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.service.JobManagementService;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JobManagementServiceImp implements JobManagementService {

    @Autowired
    PendingBICTransactionRepository pendingBICTransactionRepository;

    @Autowired
    JobManagementOutboxRepository jobManagementOutboxRepository;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    JobHistoryRepository jobHistoryRepository;

    @Autowired
    IntervalHistoryRepository intervalHistoryRepository;


    @Scheduled(fixedRate = 35000)
    public void manage(){
        Long startTime = DateTimeUtils.getCurrenTime();
        try {
            List<PendingBICTransaction> pendingBICTransactionList = pendingBICTransactionRepository.getPendingBICTransaction();
            if(pendingBICTransactionList.size() >0) {
                UUID intervalId = UUID.randomUUID();

                JobHistory jobHistory = new JobHistory();
                jobHistory.setName("PendingBICTransaction");
                jobHistory.setIntervalId(intervalId.toString());
                jobHistory.setAmountStep(5);
                jobHistory.setState("Running");
                jobHistory.setCurrentStep("0/5");

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String createAt = formatter.format(date);
                jobHistory.setCreateAt(createAt);

                jobHistoryRepository.save(jobHistory);

                for (int i = 1; i <= pendingBICTransactionList.size(); i++) {

                    PendingBICTransaction pendingBICTransaction = pendingBICTransactionList.get(i);
                    if(pendingBICTransaction.getCount() <5) {
                        IntervalHistory intervalHistory = new IntervalHistory();
                        intervalHistory.setIntervalId(intervalId.toString());
                        intervalHistory.setState("Running");
                        intervalHistory.setStep(i);

                        JSONObject stepDetail = new JSONObject();
                        stepDetail.put("requestId",pendingBICTransaction.getRequestId());
                        stepDetail.put("orderId",pendingBICTransaction.getOrderId());
                        stepDetail.put("orderReference",pendingBICTransaction.getOrderReference());
                        intervalHistory.setStepDetail(stepDetail.toString());

                        Date dateInterval = new Date();
                        String intervalCreateAt = formatter.format(dateInterval);
                        jobHistory.setCreateAt(intervalCreateAt);

                        intervalHistoryRepository.save(intervalHistory);

                        JobManagementOutbox jobManagementOutbox = new JobManagementOutbox();
                        //insert information of PendingBICTransaction into outbox
                        jobManagementOutbox.setPendingId(pendingBICTransaction.getId());
                        jobManagementOutbox.setStartTime(startTime);
                        jobManagementOutbox.setRequestId(pendingBICTransaction.getRequestId());
                        jobManagementOutbox.setOrderId(pendingBICTransaction.getOrderId());
                        jobManagementOutbox.setOrderReference(pendingBICTransaction.getOrderReference());
                        jobManagementOutbox.setFromOrderService(pendingBICTransaction.getFromOrderService());
                        jobManagementOutbox.setIntervalId(intervalId.toString());
                        jobManagementOutbox.setStep(i);
                        jobManagementOutboxRepository.save(jobManagementOutbox);
                    }
                }
            }

            //can write log?
            //for each or for all?

        }catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();

            JobManagementExceptionObject jobManagementExceptionObject = new JobManagementExceptionObject(Constant.EXCEPTION_LOG,"JobManagementService",
                    dateTimeFormatter.format(currentTime), ex.getMessage(), ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createJobManagementLogExceptionException(jobManagementExceptionObject);

        }
        System.out.println("hello");
    }
}