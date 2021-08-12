package com.smartmarket.code.service.impl;

import com.google.common.base.Throwables;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.JobManagementOutboxRepository;
import com.smartmarket.code.dao.PendingBICTransactionRepository;
import com.smartmarket.code.model.JobManagementOutbox;
import com.smartmarket.code.model.PendingBICTransaction;
import com.smartmarket.code.model.entitylog.JobManagementExceptionObject;
import com.smartmarket.code.model.entitylog.ListenerExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.service.JobManagementService;
import com.smartmarket.code.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class JobManagementServiceImp implements JobManagementService {

    @Autowired
    PendingBICTransactionRepository pendingBICTransactionRepository;

    @Autowired
    JobManagementOutboxRepository jobManagementOutboxRepository;

    @Autowired
    LogServiceImpl logService;

    //10 minutes
    @Scheduled(fixedRate = 10000)
    public void manage(){
        try {
            List<PendingBICTransaction> pendingBICTransactionList = pendingBICTransactionRepository.getPendingBICTransaction();
            for (int i = 0; i < pendingBICTransactionList.size(); i++) {

                PendingBICTransaction pendingBICTransaction = pendingBICTransactionList.get(i);
                JobManagementOutbox jobManagementOutbox = new JobManagementOutbox();

                //insert information of PendingBICTransaction into outbox
                jobManagementOutboxRepository.save(jobManagementOutbox);
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