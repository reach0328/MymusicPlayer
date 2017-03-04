package com.android.jh.mymusicplayer.util;

public class TimeUtil {
    public static String convertMiliToTime(long mili){
        long min = mili / 1000 / 60;
        long sec = mili / 1000 % 60;
        return String.format("%02d", min) + ":" + String.format("%02d",sec);
    }
}
