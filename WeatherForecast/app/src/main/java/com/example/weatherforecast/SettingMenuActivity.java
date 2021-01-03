package com.example.weatherforecast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.city_picker.CityListActivity;

public class SettingMenuActivity extends AppCompatActivity {

    private TextView tv;
    private Button loc_btn;

    private Button temp_btn;

    private Button noti_btn;
    private TextView noti_text;
    private TextView temp_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weathersetting);
        loc_btn = (Button) findViewById(R.id.Location);
        tv = (TextView) findViewById(R.id.loc_content);
        temp_text=(TextView) findViewById(R.id.temp_content);
        if(Location.sLocationName==null){
            SharedPreferences finder=MyApplication.getContext().getSharedPreferences("data", MODE_PRIVATE);
            Location.sLocationName=finder.getString("Location","");
        }
        tv.setText(Location.sLocationName);

        temp_btn=(Button) findViewById(R.id.temperature);
        if(Location.sTempType.equals("C")){
            temp_text.setText("摄氏度");
        }else{
            temp_text.setText("华氏度");
        }

        noti_btn=(Button)findViewById(R.id.notification);
        noti_text=(TextView)findViewById(R.id.notification_content);

        if(Location.isNotificationOpen){
            noti_text.setText("Open");
        }else{
            noti_text.setText("Close");
        }

        noti_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Location.isNotificationOpen){
                    Location.isNotificationOpen=false;
                    noti_text.setText("Close");
                }else{
                    Location.isNotificationOpen=true;
                    noti_text.setText("Open");
                }
            }
        });

        temp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Location.sTempType.equals("C")){
                    Location.sTempType="F";
                    temp_text.setText("华氏度");
                }else{
                    Location.sTempType="C";
                    temp_text.setText("摄氏度");
                }
            }
        });

        loc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingMenuActivity.this,SettingLocationActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onResume(){
        super.onResume();
        tv.setText(Location.sLocationName);
    }

}
