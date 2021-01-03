package com.example.weatherforecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WeatherLab {

    private Context mContext;
    private static SQLiteDatabase mDatabase;
    public static List<GalleryItem> sWeathers;

    public  WeatherLab (){
        mContext=MyApplication.getContext().getApplicationContext();
        mDatabase=new WeatherBaseHelper(mContext)
                .getWritableDatabase();
//        mWeathers=items;
        Log.i("hhh", WeatherDbSchema.WeatherTable.NAME);
    }

    public static ContentValues getContentValues(GalleryItem item){

        ContentValues values=new ContentValues();
        values.put("date",item.getData());
        values.put("tempmax",item.getTempMax());
        values.put("tempmin",item.getTempMin());
        values.put("iconday",item.getIconDay());
        values.put("textday",item.getTextDay());
        values.put("humidity",item.getHumidity());
        values.put("pressure",item.getPressure());
        values.put("windspeedday",item.getWindSpeedDay());

        return values;
    }

    public static void updateWeather(List<GalleryItem> items){
        mDatabase.delete(WeatherDbSchema.WeatherTable.NAME,null,null);

        for(int i=0;i<items.size();i++){
            ContentValues values=getContentValues(items.get(i));
            mDatabase.insert(WeatherDbSchema.WeatherTable.NAME,null,values);
        }
    }

    public List<GalleryItem> getItems(){
        List<GalleryItem> items=new ArrayList<>();

        Cursor cursor=mDatabase.query(
                WeatherDbSchema.WeatherTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                GalleryItem item = new GalleryItem();
                item.setData(cursor.getString(cursor.getColumnIndex("date")));
                item.setWindSpeedDay(cursor.getString(cursor.getColumnIndex("windspeedday")));
                item.setPressure(cursor.getString(cursor.getColumnIndex("pressure")));
                item.setHumidity(cursor.getString(cursor.getColumnIndex("humidity")));
                item.setIconDay(cursor.getString(cursor.getColumnIndex("iconday")));
                item.setTempMin(cursor.getString(cursor.getColumnIndex("tempmin")));
                item.setTempMax(cursor.getString(cursor.getColumnIndex("tempmax")));
                item.setTextDay(cursor.getString(cursor.getColumnIndex("textday")));
                items.add(item);
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return items;

    }

    public  static GalleryItem getGalleryItem(String date){
        List<GalleryItem> items=sWeathers;
        Log.i("hh",items.toString());
        for(GalleryItem item:items){
            if(item.getData().equals(date)){
                return item;
            }
        }
        return null;
    }
}
