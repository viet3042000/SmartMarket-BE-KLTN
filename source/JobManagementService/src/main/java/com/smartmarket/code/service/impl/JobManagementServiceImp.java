package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.JobManagementOutboxRepository;
import com.smartmarket.code.dao.PendingBICTransactionRepository;
import com.smartmarket.code.model.PendingBICTransaction;
import com.smartmarket.code.service.JobManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobManagementServiceImp implements JobManagementService {

    @Autowired
    PendingBICTransactionRepository pendingBICTransactionRepository;

    @Autowired
    JobManagementOutboxRepository jobManagementOutboxRepository;

    //10 minutes
    @Scheduled(fixedRate = 10000)
    public void manage(){
        List<PendingBICTransaction>pendingBICTransactionList = pendingBICTransactionRepository.getPendingBICTransaction();
        for(int i =0; i<pendingBICTransactionList.size();i++){
            //insert information of PendingBICTransaction into outbox
        }
        System.out.println("hello");
    }
}