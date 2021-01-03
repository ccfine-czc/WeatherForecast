package com.example.weatherforecast;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class WeatherForecast extends Fragment {

    private RecyclerView mDataRecycleView;

    private ConnectivityManager connectivityManager;//用于判断是否有网络

    private static String TAG="hh";
    private List<GalleryItem> mItems;
    private GalleryItem todayItem;
    private String position="101250101";
    private String positionName="长沙";
    private WeatherLab mWeatherLab;
    private TextView mDateText;
    private TextView mMaxText;
    private TextView mMinText;
    private ImageView mWeatherImage;
    private TextView mTextDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_weatherforecast,container,false);
        mDataRecycleView= (RecyclerView)view.findViewById(R.id.recycle_view);
        mDataRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDateText=(TextView)view.findViewById(R.id.date);
        mMaxText=(TextView)view.findViewById(R.id.max_temp);
        mMinText=(TextView)view.findViewById(R.id.min_temp);
        mWeatherImage=(ImageView)view.findViewById(R.id.weather_image);
        mTextDay=(TextView)view.findViewById(R.id.weather_text);
//        mTitle=(TextView)view.findViewById(R.id.title);
//        mTodayWeather=(TextView)view.findViewById(R.id.weather);
//        不能加，加了会报错
//        setupAdapter();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("hhh","jjj");
        SharedPreferences finder=MyApplication.getContext().getSharedPreferences("data", MODE_PRIVATE);
        if(finder.getString("Location",null)!=null){
            positionName=finder.getString("Location","");
            Log.i(TAG,positionName);
            connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
            NetworkInfo info = connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
            if(info!=null){
                new FetchItemsTask().execute();
            }else{
                mItems=WeatherLab.sWeathers;
                setupAdapter();
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
        NetworkInfo info = connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
        if(info!=null){
            new FetchItemsTask().execute();
        }else{
            // TODO: 2020/12/26 create中读写了数据库
            WeatherLab mlab=new WeatherLab();
            WeatherLab.sWeathers=mlab.getItems();
            mItems=WeatherLab.sWeathers;
            setupAdapter();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_weather,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.location:
//                Toast.makeText(getActivity(),"Location onclick",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:"+Location.sLat+","+Location.sLon));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            case R.id.setting:
                Intent intent2 = new Intent(getActivity(), SettingMenuActivity.class);

                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class WeatherHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView1;
        ImageView mImageView;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        GalleryItem mItem;
        String draw;
        public WeatherHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.data,parent,false));
            textView1=(TextView)itemView.findViewById(R.id.text1_view);
            textView2=(TextView)itemView.findViewById(R.id.text2_view);
            textView3=(TextView)itemView.findViewById(R.id.text_max);
            textView4=(TextView)itemView.findViewById(R.id.text_min);
            mImageView=(ImageView)itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        public boolean isTablet(Context context) {
            return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        }

        @Override
        public void onClick(View view){
//            if(view.findViewById(R.id.detail_fragment_container)==null){
//                Intent intent=WeatherActivity.newIntent(getActivity(), mItem.getData());
//                startActivity(intent);
//            }else{
            // TODO: 2020/12/25 通过以上代码无法查看当前是手机还是平板
            //如果是平板，通过fm设置细节fragment在当前界面
            if(isTablet(MyApplication.getContext())){
                Log.i("jhg",mItem.getData());
                Fragment fragment=WeatherFragment.newInstance(mItem.getData());
                FragmentManager fm=getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.detail_fragment_container,fragment)
                        .commit();
            }else{
                Intent intent=WeatherActivity.newIntent(getActivity(), mItem.getData());
                startActivity(intent);
            }

//            }
        }

        public void bindGalleryItem(GalleryItem item){
//            设置日期
            mItem=item;
            textView1.setText(new DateUtils(item.getData()).changeForm());

//            使用动态参数设置天气图标
            draw=item.getIconDay();
            draw="weather"+draw;
            Log.i(TAG,getActivity().getPackageName()+":drawable/"+draw);
            int panelId = getResources().getIdentifier(getActivity().getPackageName()+":drawable/"+draw,null,null);
            mImageView.setImageResource(panelId);

//            设置天气
            textView2.setText(item.getTextDay());

            if(Location.sTempType.equals("C")) {
                textView3.setText(item.getTempMax() + "℃");
                textView4.setText(item.getTempMin() + "℃");
            }else{
                double temp=Double.parseDouble(item.getTempMax())*1.8+32;

                String mTemp=String.format("%.1f",temp);
                textView3.setText(mTemp + "℉");
                temp=Double.parseDouble(item.getTempMin())*1.8+32;
                mTemp=String.format("%.1f",temp);
                textView4.setText(mTemp + "℉");
            }

        }

    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder>{
        private List<GalleryItem> mGalleryItems;

        public WeatherAdapter(List<GalleryItem> items){
            if(items.size()>0){
                todayItem=items.get(0);
                items.remove(0);
                mGalleryItems=items;
            }else{
                mGalleryItems=new WeatherLab().getItems();
                todayItem=mGalleryItems.get(0);
                mGalleryItems.remove(0);
            }
        }

        @NonNull
        @Override
        public WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new WeatherHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {
            if(position>=0){
                GalleryItem galleryItem=mGalleryItems.get(position);
//                Drawable placeholder=getResources().getDrawable(R.drawable.bill_up_close);
//                WeatherHolder.bindDrawable(placeholder);
                holder.bindGalleryItem(galleryItem);
            }
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void...params){
            mItems = new WeatherFetcher().fetchItems(positionName);
            return mItems;
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items){
            mItems=items;
            setupAdapter();

//            Resources resources = getResources();
//            Intent i=MainActivity.newIntent(MyApplication.getContext());
//            PendingIntent pi=PendingIntent.getActivity(MyApplication.getContext(),0,i,0);
            if(Location.isNotificationOpen){
                Notification notification = new NotificationCompat.Builder(MyApplication.getContext(), "weatherForecast")

                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle("Weather Forecast")
                        .setContentText("Forecast: "+todayItem.getTextDay()+" High:"+todayItem.getTempMax()+"℃ Low:"+todayItem.getTempMin()+"℃")
//                    .setContentIntent(pi)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notificationManager=NotificationManagerCompat.from(MyApplication.getContext());
                notificationManager.notify(0,notification);
            }

        }
    }

    private void setupAdapter(){
        if(isAdded()){
            mDataRecycleView.setAdapter(new WeatherAdapter(mItems));
            mDateText.setText(todayItem.getData());

            if(Location.sTempType.equals("C")) {
                mMaxText.setText(todayItem.getTempMax() + "℃");
                mMinText.setText(todayItem.getTempMin() + "℃");
            }else{
                double temp=Double.parseDouble(todayItem.getTempMax())*1.8+32;
                String mTemp=String.format("%.1f",temp);
                mMaxText.setText(mTemp + "℉");

                temp=Double.parseDouble(todayItem.getTempMin())*1.8+32;
                mTemp=String.format("%.1f",temp);
                mMinText.setText(mTemp + "℉");
            }

            String draw=todayItem.getIconDay();
            draw="weather"+draw;
            Log.i(TAG,getActivity().getPackageName()+":drawable/"+draw);
            int panelId = getResources().getIdentifier(getActivity().getPackageName()+":drawable/"+draw,null,null);
            mWeatherImage.setImageResource(panelId);

            mTextDay.setText(todayItem.getTextDay());

        }
    }
}
