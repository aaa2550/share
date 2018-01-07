package com.mak.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2018/1/6.
 */
public class DateUtil {

    public final static String DEFAULT = "yyyy-MM-dd";

    public final static String DEFAULT_TIME = "yyyy-MM-dd HH:mm:ss";

    public static DateFormat getDateFormat() {
        return getDateFormat(DEFAULT);
    }

    public static DateFormat getDateFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public static Date parse(String date) {
        return parse(date, DEFAULT);
    }

    public static Date parse(String date, String format) {
        try {
            return getDateFormat(format).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String format(Date date) {
        return format(date, DEFAULT);
    }

    public static String format(Date date, String format) {
        return getDateFormat(format).format(date);
    }

}
