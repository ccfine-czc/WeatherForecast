package com.example.weatherforecast;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.weatherforecast.WeatherDbSchema.WeatherTable;

public class WeatherBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION=1;
    private static final String DATABASE_NAME="weatherBase.db";

    public WeatherBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
//        db.execSQL("drop table "+WeatherTable.NAME);
        db.execSQL("create table "+ WeatherTable.NAME+"("+
                "date primary key"+ ", "+
                "tempmax"+", "+
                "tempmin"+", "+
                "iconday"+", "+
                "textday"+", "+
                "humidity"+", "+
                "pressure"+", "+
                "windspeedday"+")"
        );
        Log.i("ccc",WeatherTable.NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }
}
