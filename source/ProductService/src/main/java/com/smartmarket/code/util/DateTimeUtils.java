package com.smartmarket.code.util;

import com.smartmarket.code.constants.Format;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {


    private static final int JavaDate_StartYear = 1900;
    private static final String ZERO = "0";
    private static TimeZone tz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    public static Long getCurrenTime() {

        return System.currentTimeMillis();
    }

    public static Long getCurrentTimeRaw(){
        return getCurrenTime() + tz.getRawOffset();
    }

    public static Date getCurrentDateRaw() {
        Date d = new Date(getCurrenTime() + tz.getRawOffset()) ;
        return d;
    }

    public static Date getConvertLongToDate(Long time) {
        Date d = new Date(time + tz.getRawOffset()) ;
        return d;
    }

    public static Long getConvertDateToLongStart(Date date) {
        if(date !=null){
            Calendar now = Calendar.getInstance();
            now.setTime(date);
            now.set(Calendar.HOUR, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);
            return now.getTimeInMillis();
        }
        return null;
    }
    public static Long getConvertDateToLongEnd(Date date) {
        if(date !=null) {
            Calendar now = Calendar.getInstance();
            now.setTime(date);
            now.set(Calendar.HOUR, 23);
            now.set(Calendar.MINUTE, 59);
            now.set(Calendar.SECOND, 59);
            now.set(Calendar.MILLISECOND, 999);
            return now.getTimeInMillis();
        }
        return null;
    }


    public static void main(String[] args) {
        System.out.println("=======>start" + getConvertDateToLongStart(getConvertLongToDate(getCurrenTime())));
        System.out.println("=======>end" + getConvertDateToLongEnd(getConvertLongToDate(getCurrenTime())));

        System.out.printf("" + new Date().getTime());
        System.out.printf("\n" + System.currentTimeMillis());
        Instant instant = Instant.now();
        System.out.println("\n" + Instant.now().toEpochMilli());
        System.out.println("\n" + getCurrentTimeRaw());
    }
    public static String format_yyyyMMdd(java.util.Date d) {
        if (d == null) {
        }
        String year = getYear(d);
        String month = getMonth(d);
        String date = getDate(d);
        return String.format(Format.DATE_FORMAT, year, month, date);
    }
    public static String getDate(java.util.Date d) {
        return format2DNumber(d.getDate());
    }

    public static String getMonth(java.util.Date d) {
        return format2DNumber(d.getMonth() + 1);
    }

    public static String getYear(java.util.Date d) {
        return format2DNumber(d.getYear() + JavaDate_StartYear);
    }

    private static String format2DNumber(int n) {
        return n > 9 ? String.valueOf(n) : (ZERO + n);
    }

    public static String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }


    public static Date getStringToDate(String dateStr){
        Date date = null ;
        try {
             date =new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date ;
    }

    public static String getElapsedTimeStr(long startTime){
        long endTime = System.currentTimeMillis() ;
        long elapsed = endTime - startTime;
        return Long.toString(elapsed) ;
    }

    public static Long getElapsedTime(long startTime){
        long endTime = System.currentTimeMillis() ;
        long elapsed = endTime - startTime;
        return elapsed;
    }

    public static Long getStartTimeFromRequest(HttpServletRequest request){
        Long startTime =  null ;
        if(request != null ){
            startTime =(Long) request.getAttribute("startTime") ;
        }
        return startTime ;
    }

}
