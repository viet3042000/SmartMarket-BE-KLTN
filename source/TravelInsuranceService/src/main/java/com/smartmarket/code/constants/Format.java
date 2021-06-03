package com.smartmarket.code.constants;

public class Format {
    public static final String EMAIL_FORMAT = "^((([0-9a-zA-Z]((\\.(?!\\.))|[-!#\\$%&'\\*\\+/=\\?\\^`\\{\\}\\|~\\w])*)(?<=[0-9a-zA-Z])@))((\\[(\\d{1,3}\\.){3}\\d{1,3}\\])|(([0-9a-zA-Z-]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,6}))$";

    public static final String PHONE_FORMAT = "\\+(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|"
            + "2[98654321]\\d|1[6]\\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|"
            + "4[987654310]|3[9643210]|2[70]|7|1)\\d{1,14}$";
    public static final String URL_REGEX = "^(ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\'\\/\\\\\\+&amp;%\\$#_=]*)?$";

    public static final String DATE_TIME = "yyyyMMddHHmmss";
    public static final String DATE_TIME_SECON = "yyyyMMddHHmm";
    public static final String DATE_TIME_FORMAT = "%s%s%s%s%s%s";
    public static final String DATE_TIME_MINUTE_FORMAT = "%s%s%s%s%s";

    public static final String DATE = "yyyyMMdd";
    public static final String HOUR_MINUTE = "HHmm";
    public static final String DATE_FORMAT = "%s%s%s";

    public static final String MONTH = "yyyyMM";
    public static final String MONTH_FORMAT = "%s%s";

    public static final String DISPLAY_TIME_FORMAT = "%s:%s on %s/%s/%s";

    public static final String DATE_HOUR = "yyyyMMddHH";
    public static final String DATE_HOUR_FORMAT = "%s%s%s%s";

    public static final String DATE_TIME_MILLISECON = "yyyyMMddHHmmssSSS";
    public static final String DATE_TIME_MILLISECON_FORMAT = "%s%s%s%s%s%s%s";
}
