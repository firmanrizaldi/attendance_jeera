package com.example.jeera_attendance;

import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Helper {

    public static String getTimeStamp(String pattern) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return String.valueOf(sdf.format(timestamp));

    }

    public static String dateFormatIn(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        TimeZone gmtTime = TimeZone.getTimeZone("GMT+7");
        outputFormat.setTimeZone(gmtTime);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            str = outputFormat.format(calendar.getTime()).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str + " 00:00:00";
    }

    public static String dateFormatOut(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        TimeZone gmtTime = TimeZone.getTimeZone("GMT+7");
        outputFormat.setTimeZone(gmtTime);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            str = outputFormat.format(calendar.getTime()).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str + " 23:59:59";
    }

    public static String dateTimeFormat(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        TimeZone gmtTime = TimeZone.getTimeZone("GMT+7");
        outputFormat.setTimeZone(gmtTime);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, -7);
            calendar.add(Calendar.MINUTE, 0);
            str = outputFormat.format(calendar.getTime()).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String dateTimeFormatTo(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        TimeZone gmtTime = TimeZone.getTimeZone("GMT+7");
        outputFormat.setTimeZone(gmtTime);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, +7);
            calendar.add(Calendar.MINUTE, 0);
            str = outputFormat.format(calendar.getTime()).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static Integer cekCheckin(String timecurent, String timecheckin) {
        String Current = timecurent ;
        String Checkin = timecheckin ;
        String outputPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date d1 = null;
        Date d2 = null;
        Integer str = null;
        try {
            d1 = outputFormat.parse(timecurent);
            d2 = outputFormat.parse(Checkin);
            long diff = d1.getTime() - d2.getTime() ;
            long diffMinutes = diff / (60 * 1000) % 60;
            str = Math.toIntExact(diffMinutes);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String Base64(ImageView File) {
        String info;
        File.buildDrawingCache();
        Bitmap bitmap = File.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image = stream.toByteArray();
        info = Base64.encodeToString(image, 0);
        return info;
    }

}
