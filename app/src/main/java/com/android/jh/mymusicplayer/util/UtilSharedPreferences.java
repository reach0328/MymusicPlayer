package com.android.jh.mymusicplayer.util;


import android.content.Context;
import android.content.SharedPreferences;

public class UtilSharedPreferences {
    private static final String APPLICATION = "com.android.jh.mymusicplayer.util";


    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(APPLICATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static int loadInt(Context context,String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(APPLICATION, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

}
