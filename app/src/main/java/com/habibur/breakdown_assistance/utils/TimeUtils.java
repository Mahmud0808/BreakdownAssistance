package com.habibur.breakdown_assistance.utils;

import android.content.Context;

import com.habibur.breakdown_assistance.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String convertLongToTime(Context context, String format, long time) {
        if (time == 0) {
            return context.getString(R.string.not_available);
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date resultdate = new Date(time);
        return sdf.format(resultdate);
    }
}
