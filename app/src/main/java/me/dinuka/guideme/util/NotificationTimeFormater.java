package me.dinuka.guideme.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationTimeFormater {
    public static String getTimeAgo(long notifiedTimeMs) {
        long now = System.currentTimeMillis();
        long diff = now - notifiedTimeMs;

        long SECOND = 1000;
        long MINUTE = 60 * SECOND;
        long HOUR = 60 * MINUTE;
        long DAY = 24 * HOUR;
        long WEEK = 7 * DAY;

        if (diff < MINUTE) {
            return "Just now";
        } else if (diff < 2 * MINUTE) {
            return "1 minute ago";
        } else if (diff < 60 * MINUTE) {
            return (diff / MINUTE) + " minutes ago";
        } else if (diff < 2 * HOUR) {
            return "1 hour ago";
        } else if (diff < 24 * HOUR) {
            return (diff / HOUR) + " hours ago";
        } else if (diff < 2 * DAY) {
            return "Yesterday";
        } else if (diff < 7 * DAY) {
            return (diff / DAY) + " days ago";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return sdf.format(new Date(notifiedTimeMs));
        }
    }
}
