package com.na21k.schedulenotes.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHelper {

    public static Date truncateSecondsAndMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static String getScheduleFormattedDate(Date date) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
        return dateFormat.format(date);
    }

    public static String getScheduleFormattedTime(Date date) {
        DateFormat timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        return timeFormat.format(date);
    }
}
