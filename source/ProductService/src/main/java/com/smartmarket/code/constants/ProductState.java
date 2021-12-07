package com.smartmarket.code.constants;

public class ProductState {
    public static final String PENDING = "Pending";//before all provider approved
    public static final String APPROVED = "Approved";//after all provider had approved
    public static final String DISAPPROVED = "DisApproved";

//    public static final String CANCELING = "Canceling";
//    public static final String CANCELED = "Canceled";
//    public static final String ABORTING = "Aborting";
//    public static final String ABORTED = "Aborted";

    public static final String ERROR = "Error";//exception when approve
}
