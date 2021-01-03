package com.example.weatherforecast;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private String mDate;

    public DateUtils(String date){
        mDate=date;
    }

    public String changeForm(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String res=null;
        try{
            Date targetDay=sdf.parse(mDate);
            String temp = sdf.format(new Date(System.currentTimeMillis()));
            Date today=sdf.parse(temp);
            long ts = targetDay.getTime();
            long td = today.getTime();
            long days=(ts - td) / (1000 * 60 * 60 * 24);

            if(days==0) res = "Today";
            else if(days==1) res = "Tomorrow";
            else res = dateToWeek(mDate);
            Log.i("hh",res);

        }catch (ParseException pex){
            Log.i("DATE","parse wrong");
        }

        return res;
    }

    public String getMonthToEnglish(){
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] month={"January","February","March","April","May","June","July","August",
        "September","October","November","December"};
        int mon=1;
        try{
            Date date=f.parse(mDate);

            mon=date.getMonth();


        }catch (ParseException e){
            e.printStackTrace();
        }
        return month[mon];
    }

    public String getDayToEnglish(){
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        int day=1;
        try{
            Date date=f.parse(mDate);
            cal.setTime(date);
            day = cal.get(Calendar.DATE);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return String.valueOf(day);
    }

    public String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date date = null;
        try {
            date = f.parse(datetime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}
