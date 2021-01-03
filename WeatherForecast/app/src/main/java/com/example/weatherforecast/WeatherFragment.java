package com.example.weatherforecast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class WeatherFragment extends Fragment {
    private static String WeatherID="date";

    private TextView mTextViewWeek;
    private TextView mTextViewDate;
    private TextView mMaxTemp;
    private TextView mMinTemp;
    private TextView mWeather;
    private ImageView mWeatherImage;
    private TextView mHumidity;
    private TextView mPressure;
    private TextView mWind;
    private  GalleryItem mItem;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        String weatherID=(String)getActivity().getIntent().getSerializableExtra(WeatherActivity.EXTRA_Weather_ID);
//        mItem=new WeatherLab().getGalleryItem(weatherID);
//        weatherID=(String)getArguments().getSerializable(WeatherID);
        String weatherID=(String)getArguments().getSerializable(WeatherID);
        Log.i("hhh",weatherID);
        mItem=WeatherLab.getGalleryItem(weatherID);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.detail_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.sharing:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "天气");
                intent.putExtra(Intent.EXTRA_TEXT,new DateUtils(mItem.getData()).getMonthToEnglish()+" "+new DateUtils(mItem.getData()).getDayToEnglish()+": "+mItem.getTextDay()+" 最高温度:"+mItem.getTempMax()+"℃ 最低气温:"+mItem.getTempMin()+"℃");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            case R.id.location:
//                Toast.makeText(getActivity(),"Location onclick",Toast.LENGTH_SHORT).show();

                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse("geo:"+Location.sLat+","+Location.sLon));
                if (intent2.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent2);
                }
                return true;
            case R.id.setting:
                Intent intent3 = new Intent(getActivity(), SettingMenuActivity.class);

                startActivity(intent3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static WeatherFragment newInstance(String weatherId) {
        Bundle args = new Bundle();
        args.putSerializable(WeatherID,weatherId);

        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        View v=inflater.inflate(R.layout.fragment_weather,container,false);
        mTextViewWeek=(TextView)v.findViewById(R.id.week);
        mTextViewWeek.setText(new DateUtils(mItem.getData()).changeForm());
        mTextViewDate=(TextView)v.findViewById(R.id.date);
        mTextViewDate.setText(new DateUtils(mItem.getData()).getMonthToEnglish()+" "+new DateUtils(mItem.getData()).getDayToEnglish());
        mMaxTemp=(TextView)v.findViewById(R.id.max_temp);
        if(Location.sTempType.equals("C")) {
            mMaxTemp.setText(mItem.getTempMax() + "℃");
        }else{
            double temp=Double.parseDouble(mItem.getTempMax())*1.8+32;
            String mTemp=String.format("%.1f",temp);
            mMaxTemp.setText(mTemp + "℉");
        }

        mMinTemp=(TextView)v.findViewById(R.id.min_temp);
        if(Location.sTempType.equals("C")) {
            mMinTemp.setText(mItem.getTempMin() + "℃");
        }else{
            double temp=Double.parseDouble(mItem.getTempMin())*1.8+32;
            String mTemp=String.format("%.1f",temp);
            mMinTemp.setText(mTemp + "℉");
        }

        mWeatherImage=(ImageView)v.findViewById(R.id.weather_image);
        String draw=mItem.getIconDay();
        draw="weather"+draw;
        int panelId = getResources().getIdentifier(getActivity().getPackageName()+":drawable/"+draw,null,null);
        mWeatherImage.setImageResource(panelId);

        mWeather=(TextView)v.findViewById(R.id.weather_text);
        mWeather.setText(mItem.getTextDay());

        mHumidity=(TextView)v.findViewById(R.id.humidity_text);
        mHumidity.setText("Humidity: "+mItem.getHumidity()+" %");

        mPressure=(TextView)v.findViewById(R.id.pressure_text);
        mPressure.setText("Pressure: "+mItem.getPressure()+" hPa");

        mWind=(TextView)v.findViewById(R.id.wind_text);
        mWind.setText("Wind: "+mItem.getWindSpeedDay()+" Km/h SE");

        return v;
    }
}
