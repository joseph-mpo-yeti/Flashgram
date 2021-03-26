package com.josephmpo.flashgram;

import java.util.Calendar;
import java.util.Locale;


public class TimeFormatter {
    public static String getTimeDifference(long timestamp) {
        String time = "";
        long diff = (System.currentTimeMillis() - timestamp) / 1000;
        if (diff < 5)
            time = "Just now";
        else if (diff < 60)
            time = String.format(Locale.ENGLISH, "%ds",diff);
        else if (diff < 60 * 60)
            time = String.format(Locale.ENGLISH, "%dm", diff / 60);
        else if (diff < 60 * 60 * 24)
            time = String.format(Locale.ENGLISH, "%dh", diff / (60 * 60));
        else if (diff < 60 * 60 * 24 * 30)
            time = String.format(Locale.ENGLISH, "%dd", diff / (60 * 60 * 24));
        else {
            Calendar now = Calendar.getInstance();
            Calendar then = Calendar.getInstance();
            then.setTimeInMillis(timestamp);
            if (now.get(Calendar.YEAR) == then.get(Calendar.YEAR)) {
                time = String.valueOf(then.get(Calendar.DAY_OF_MONTH)) + " "
                        + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
            } else {
                time = String.valueOf(then.get(Calendar.DAY_OF_MONTH)) + " "
                        + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                        + " " + String.valueOf(then.get(Calendar.YEAR) - 2000);
            }
        }

        return time;
    }

}

