package com.example.justin.verbeterjegemeente.Business;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by twanv on 7-8-2017.
 */

// TODO: 22-8-2017  add javadoc
public class TimeStampGenerator {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

    public static String genOlderTimestamp() {
        Date currentDate = new Date();

        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        // add time/subtract time to calender
        c.add(Calendar.WEEK_OF_MONTH, -2);

        // convert calendar to date
        Date currentDateMinus2Weeks = c.getTime();

        return simpleDateFormat.format(currentDateMinus2Weeks);
    }
}
