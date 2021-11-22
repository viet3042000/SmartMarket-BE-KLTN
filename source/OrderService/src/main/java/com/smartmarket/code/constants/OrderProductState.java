package com.smartmarket.code.constants;

public class OrderProductState {
    //Succeeded/Canceling/Canceled/Aborted/Aborting/Error
    public static final String SUCCEEDED = "Succeeded";//create Succeeded
    public static final String CANCELING = "Canceling";//cancel
    public static final String CANCELED = "Canceled";//cancel Succeeded
    public static final String ABORTING = "Aborting";
    public static final String ABORTED = "Aborted";
    public static final String ERROR = "Error";//Aborting failure
}
