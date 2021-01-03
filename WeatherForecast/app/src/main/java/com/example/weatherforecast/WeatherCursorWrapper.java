package com.example.weatherforecast;

import android.database.Cursor;
import android.database.CursorWrapper;

public class WeatherCursorWrapper extends CursorWrapper {
    public WeatherCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public GalleryItem getWeather() {
        GalleryItem item = new GalleryItem();
        item.setData(getString(getColumnIndex("date")));
        item.setWindSpeedDay("windspeedday");
        item.setPressure("pressure");
        item.setHumidity("humidity");
        item.setIconDay("iconday");
        item.setTempMin("tempmin");
        item.setTempMax("tempmax");
        item.setTextDay("textday");
        return item;
    }
}

