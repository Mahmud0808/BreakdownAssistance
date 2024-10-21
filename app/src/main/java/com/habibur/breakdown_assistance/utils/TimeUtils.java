package com.habibur.breakdown_assistance.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String convertLongToTime(String format, long time) {
        if (time == 0) {
            return "N/A";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date resultdate = new Date(time);
        return sdf.format(resultdate);
    }
}
