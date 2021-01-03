package com.example.weatherforecast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.city_picker.CityListActivity;

public class SettingLocationActivity extends AppCompatActivity {

    private TextView tv;
    private ConnectivityManager connectivityManager;//用于判断是否有网络

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationsetting);
        Button btn = (Button) findViewById(R.id.btn);
        tv = (TextView) findViewById(R.id.name);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityListActivity.startCityActivityForResult(SettingLocationActivity.this);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101&& resultCode ==102){
            String city = data.getStringExtra("city");

            tv.setText(city);

            connectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
            NetworkInfo info = connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
            if(info!=null){
                SharedPreferences.Editor editor=MyApplication.getContext().getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("Location",city);
                editor.commit();
            }
            Log.i("loc",city);
            Location.sLocationName=city;
        }
    }
}
