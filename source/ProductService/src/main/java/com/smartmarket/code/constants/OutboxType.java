package com.smartmarket.code.constants;

public class OutboxType {

    public static final String WAITING_APPROVE = "waitingApprove";

    //create: created -->create in db --> approval finished --> update state
    public static final String APPROVE_CREATED_PRODUCT = "approveCreatedProduct";

    //update + delete : approval finished --> update / delete in db
    public static final String APPROVE_UPDATING_PRODUCT = "approveUpdatingProduct";

    public static final String APPROVE_DELETING_PRODUCT = "approveDeletingProduct";
}
