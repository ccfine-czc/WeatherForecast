package com.example.weatherforecast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.util.UUID;

public class WeatherActivity extends SingleFragmentActivity {
    public static final String EXTRA_Weather_ID =
            "com.example.weather_intent.weather_id";
//
    public static Intent newIntent(Context packageContext, String date) {
        Intent intent = new Intent(packageContext, WeatherActivity.class);
        intent.putExtra(EXTRA_Weather_ID, date);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        String date=(String) getIntent().getSerializableExtra(EXTRA_Weather_ID);
        return WeatherFragment.newInstance(date);
    }

}