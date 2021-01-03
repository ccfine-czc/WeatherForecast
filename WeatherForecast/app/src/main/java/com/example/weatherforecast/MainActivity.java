package com.example.weatherforecast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context){
        return new Intent(context,MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        WeatherLab mlab=new WeatherLab();

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道实例（这三个参数是必须要有的）
            NotificationChannel channel = new NotificationChannel("weatherForecast", "天气预报", NotificationManager.IMPORTANCE_HIGH);
            //创建通知渠道的通知管理器
            NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
            //将实例交给管理器
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected Fragment createFragment(){
        return new WeatherForecast();
//        return new Fragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }
}