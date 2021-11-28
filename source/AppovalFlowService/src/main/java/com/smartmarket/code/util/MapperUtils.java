package com.smartmarket.code.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

//scope to refresh fieldsConstants and hostConstants
@Component
//@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MapperUtils {

    @Autowired
    ConfigurableEnvironment environment;


    public static boolean getBool(Long value) {
        if (value.equals(1L)) {
            return true;
        }
        return false;
    }

    public static Long getLongFromBool(Boolean value) {
        if (value != null) {
            if (value == true) {
                return 1L;
            }
            return 0L;
        }
        return null;
    }


    public static String convertDOB(String DOB){
        String DOBResponse = "" ;
        if (!StringUtils.isEmpty(DOB)){
            DOBResponse = DOB.substring(0,DOB.indexOf("T")) ;
            return DOBResponse ;
        }
        return DOBResponse ;
    }

}
