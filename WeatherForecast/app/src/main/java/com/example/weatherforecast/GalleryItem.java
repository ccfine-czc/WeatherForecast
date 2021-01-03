package com.example.weatherforecast;

public class GalleryItem {
    private String mData;
    private String mTempMax;
    private String mTempMin;
    private String mIconDay;
    private String mTextDay;
    private String mHumidity;
    private String mPressure;
    private String mWindSpeedDay;
    private String tempType;

    public String getTempType() {
        return tempType;
    }

    public void setTempType(String tempType) {
        this.tempType = tempType;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public void setHumidity(String humidity) {
        mHumidity = humidity;
    }

    public void setPressure(String pressure) {
        mPressure = pressure;
    }

    public String getPressure() {
        return mPressure;
    }

    public String getWindSpeedDay() {
        return mWindSpeedDay;
    }

    public void setWindSpeedDay(String windSpeedDay) {
        mWindSpeedDay = windSpeedDay;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }

    public String getTempMin() {
        return mTempMin;
    }

    public void setTempMin(String tempMin) {
        mTempMin = tempMin;
    }

    public String getTextDay() {
        return mTextDay;
    }

    public void setTextDay(String textDay) {
        mTextDay = textDay;
    }

    public String getIconDay() {
        return mIconDay;
    }

    public void setIconDay(String iconDay) {
        mIconDay = iconDay;
    }

    public String getTempMax() {
        return mTempMax;
    }

    public void setTempMax(String tempMax) {
        mTempMax = tempMax;
    }
}
