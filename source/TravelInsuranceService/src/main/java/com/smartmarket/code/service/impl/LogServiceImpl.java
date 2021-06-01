package com.smartmarket.code.service.impl;

import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.exception.RestControllerHandleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class LogServiceImpl  {

    private Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    public void createTargetLog(String TRANSACTIONID,      String MESSAGEID,     String IIBMESSAGEID,
                                   String TARGETSERVICE,      String MESSAGETYPE,   String TRANSACTIONDETAIL,
                                   String LOGTIMESTAMP ,      String MESSAGETIMESTAMP,String TIMEDURATION) throws CustomException {


		logger.info(TRANSACTIONID  +"%5" + MESSAGEID  +"%5" + IIBMESSAGEID +"%5"+ TARGETSERVICE +"%5"+
                       MESSAGETYPE    +"%5"+ TRANSACTIONDETAIL  +"%5"+ LOGTIMESTAMP +"%5"+MESSAGETIMESTAMP +"%5"+ TIMEDURATION);

	}




	public void createSOALog(String MESSAGEID,        String TRANSACTIONID,       String SOURCEID,      String TARGETID,
                               String MESSAGETIMESTAMP, String SERVICENAME,         String OPERATIONNAME, String SERVICEVERSION,
                               String TIMEDURATION,     String STATUS,              String ADDITIONALMSG, String TRANSACTIONDETAIL,
                               String RESPONSESTATUS,   String ERRORCODE,           String ERRORMSG  ) throws CustomException {

        String CLIENTIP = getIp() ;

        logger.info(MESSAGEID  +"%5" + TRANSACTIONID  +"%5" + SOURCEID +"%5"+ TARGETID +"%5"+
                MESSAGETIMESTAMP    +"%5"+ SERVICENAME  +"%5"+ OPERATIONNAME +"%5"+SERVICEVERSION +"%5"+ TIMEDURATION
                +"%5"+ STATUS +"%5"+ SERVICENAME +"%5"+ ADDITIONALMSG +"%5"+ TRANSACTIONDETAIL +"%5"+ RESPONSESTATUS
                +"%5"+ ERRORCODE +"%5"+ ERRORMSG +"%5"+ CLIENTIP  ) ;

	}
	private String getIp() {
        String serverId;
        try {
            serverId = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            serverId = "127.0.0.1";
        }
        return serverId;
    }
}
