[toc]

### 一、 问题描述

​	1.  在手机中包含主视图和细节视图，主视图显示连续多天的天气预报简讯，如图表 1 所示，用户在主视图中点		击某一天的天气简讯以后，跳出细节视图，显示用户选定当天天气的详细信息。 

​	2. 在平板中使用Master-detail视图，当用户点击某一天的天气预览以后，直接在界面右边显示当天天气的详细		信息，如图表 3所示。 

​	3. 主视图中包含Map Location和setting选项，通过”Map location” 选项，可以调用手机中安装的地图应用显示		当前天气预报所对应的位置，如图表 4所示，用户可以通过setting选项可以修改天气预报的位置，温度的单		位（华氏度、摄氏度）以及是否开启天气通知，如图表 5所示。如果setting选项中的天气通知选项打开，会		定期发送通知消息，其中显示当天的天气简讯，如图表 6所示。 

4. 细节视图菜单中包含分享和setting选项，用户可以通过分享选项通过其他应用（邮件、短信等）将天气详细信息分享给别人。如图表2所示。 

5.  利用SQLite对天气预报数据进行持久化保存，如果网络不可用的情况下，从SQLite中提取天气预报数据。

6.  Web API请使用https://www.heweather.com/documents/api/s6/weather-forecast



### 二、 设计简要描述

+ **功能一：**在主视图中使用一个view显示当天天气，下面使用recycleview显示之后的天气。当爬取天气信息的线程得到数组形式的信息后，将第一个下标信息给当天的view，剩下的给recycleview。

  在https://www.heweather.com/documents/api/s6/weather-forecast的到的天气信息是日期形式，本题需要显示的是today、tomorrow和星期形式。写一个DateUtil类，将日期转为需要的形式

  ```java
  package com.example.weatherforecast;
  
  import android.util.Log;
  
  import java.text.ParseException;
  import java.text.SimpleDateFormat;
  import java.util.Calendar;
  import java.util.Date;
  
  public class DateUtils {
      private String mDate;
  
      public DateUtils(String date){
          mDate=date;
      }
  
      public String changeForm(){
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          String res=null;
          try{
              Date targetDay=sdf.parse(mDate);
              String temp = sdf.format(new Date(System.currentTimeMillis()));
              Date today=sdf.parse(temp);
              long ts = targetDay.getTime();
              long td = today.getTime();
              long days=(ts - td) / (1000 * 60 * 60 * 24);
  
              if(days==0) res = "Today";
              else if(days==1) res = "Tomorrow";
              else res = dateToWeek(mDate);
              Log.i("hh",res);
  
          }catch (ParseException pex){
              Log.i("DATE","parse wrong");
          }
  
          return res;
      }
  
      public String getMonthToEnglish(){
          SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
          String[] month={"January","February","March","April","May","June","July","August",
          "September","October","November","December"};
          int mon=1;
          try{
              Date date=f.parse(mDate);
  
              mon=date.getMonth();
  
  
          }catch (ParseException e){
              e.printStackTrace();
          }
          return month[mon];
      }
  
      public String getDayToEnglish(){
          SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
  
          int day=1;
          try{
              Date date=f.parse(mDate);
  
              day=date.getDay();
  
          }catch (ParseException e){
              e.printStackTrace();
          }
          return String.valueOf(day);
      }
  
      public String dateToWeek(String datetime) {
          SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
          String[] weekDays = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
          Calendar cal = Calendar.getInstance(); // 获得一个日历
          Date date = null;
          try {
              date = f.parse(datetime);
              cal.setTime(date);
          } catch (ParseException e) {
              e.printStackTrace();
          }
          int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
          if (w < 0)
              w = 0;
          return weekDays[w];
      }
  }
  ```

  点击recycleview时，通过调用WeatherActivity中的newIntent方法启动WeatherActivity，并将日期传递过去

  WeatherForecast.java

  ```
  Intent intent=WeatherActivity.newIntent(getActivity(), mItem.getData());
  startActivity(intent);
  ```

  WeatherActivity.java

  ```
  public static Intent newIntent(Context packageContext, String date) {
      Intent intent = new Intent(packageContext, WeatherActivity.class);
      intent.putExtra(EXTRA_Weather_ID, date);
      return intent;
  }
  ```

  WeatherActivity的onCreate中通过调用WeatherFragment中的newInstance启动一个显示天气细节的Fragment，并将日期信息传递过去。

  ```
  @Override
  protected Fragment createFragment() {
       String date=(String) getIntent().getSerializableExtra(EXTRA_Weather_ID);
       return WeatherFragment.newInstance(date);
  }
  ```

  在WeatherFragment中通过日期在WeatherLab中找到其它天气信息

  ```
  public  static GalleryItem getGalleryItem(String date){
      List<GalleryItem> items=sWeathers;
      for(GalleryItem item:items){
          if(item.getData().equals(date)){
             return item;
          }
      }
      return null;
  }
  ```

  

+ **功能二:**在主xml中建立两个FrameLayout，其中一个是detail视图，在WeatherForecast启动时，检查当前设备是手机还是平板

  ```
  public boolean isTablet(Context context) {
  	return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
  }
  ```

  若是平板，通过fm设置细节fragment在当前界面

  ```
  Fragment fragment=WeatherFragment.newInstance(mItem.getData());
  FragmentManager fm=getActivity().getSupportFragmentManager();
  fm.beginTransaction()
    .replace(R.id.detail_fragment_container,fragment)
    .commit();
  ```

  否则启动Activity

  ```
  Intent intent=WeatherActivity.newIntent(getActivity(), mItem.getData());
  startActivity(intent);
  ```

  

+ **功能三：**打开地图时，通过当前城市名称，在https://geoapi.qweather.com/v2/city/lookup获得该城市的经纬度，使用隐式intent打开地图，并传入经纬度

  ```
  Intent intent = new Intent(Intent.ACTION_VIEW);
  intent.setData(Uri.parse("geo:"+Location.sLat+","+Location.sLon));
  if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
      startActivity(intent);
  }
  ```

  设置位置时，打开一个选择位置的Activity，选择位置后，将位置通过一个天气信息的位置静态变量存储设置的位置，并通过文件保存。返回时刷新当前信息。启动位置Activity时WeatherForecast会onPause，位置Activity退出后WeatherForecast的Activity会onResume，因此在onResume时写上刷新界面的操作。

  

+ **功能四：**分享使用隐式intent，用参数Intent.ACTION_SEND打开邮件。

  ```
  Intent intent = new Intent(Intent.ACTION_SEND);
  intent.setType("*/*");
  intent.putExtra(Intent.EXTRA_SUBJECT, "今日天气");
  intent.putExtra(Intent.EXTRA_TEXT,mItem.getIconDay()+" 最高温度:"+mItem.getTempMax()+" 最低气温:"+mItem.getTempMin());
  if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
      startActivity(intent);
  }
  ```



+ **功能五：**在启动时，检查网络是否连接

  ```
  connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
  NetworkInfo info = connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
  ```

  若有网络，在得到天气信息后，存入数据库。

  若没有网络，通过数据库得到天气信息。



### 三、 程序清单

​	WeatherForecast.java(主界面的类)

```
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
                        .setContentText("Forecast: "+todayItem.getIconDay()+" High:"+todayItem.getTempMax()+"℃ Low:"+todayItem.getTempMin()+"℃")
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
```



### 四、 结果分析

​                                                                                     1. 主视图

​																<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101135509640.png" alt="image-20210101135509640" style="zoom:25%;" />



​																					2. 细节视图

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101141059861.png" alt="image-20210101141059861" style="zoom:25%;" />



​																					3. 平板视图

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101140325416.png" alt="image-20210101140325416" style="zoom: 50%;" />



<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101140349788.png" alt="image-20210101140349788" style="zoom:50%;" />



​																					4. map和setting

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101141443874.png" alt="image-20210101141443874" style="zoom:25%;" />



​																		打开地图显示当前位置

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101141629197.png" alt="image-20210101141629197" style="zoom:25%;" />



​																	点击setting修改位置

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101141825200.png" alt="image-20210101141825200" style="zoom:25%;" />

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101141853965.png" alt="image-20210101141853965" style="zoom:25%;" />

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101141913445.png" alt="image-20210101141913445" style="zoom:25%;" />

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101141925865.png" alt="image-20210101141925865" style="zoom:25%;" />



​																			修改温度单位为华氏度

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101142140401.png" alt="image-20210101142140401" style="zoom:25%;" />

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101142153817.png" alt="image-20210101142153817" style="zoom:25%;" />



​																			     打开通知

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101142252273.png" alt="image-20210101142252273" style="zoom:25%;" />



​																		细节视图通过邮件分享天气

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101142357378.png" alt="image-20210101142357378" style="zoom:25%;" />

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101142413186.png" alt="image-20210101142413186" style="zoom:25%;" />

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101142427488.png" alt="image-20210101142427488" style="zoom:25%;" />

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101142532929.png" alt="image-20210101142532929" style="zoom: 50%;" />



​																	5. 持久化保存

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101142943134.png" alt="image-20210101142943134" style="zoom:25%;" />

<img src="C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210101142957101.png" alt="image-20210101142957101" style="zoom:25%;" />



### 五、 调试报告

1. sharedpreference存数据时需要commit，且取出判断是否存在时

   ```
   if(finder.getString("Location",null)!=null)          
   ```

   而不是

   ```
   finder.getString("Location","")!=null)
   ```

   第二种写法找不到默认返回"",而不是null，if语句永远为真

   

2. 创建平板设备专用资源方法，res下新建Android resource file。

   

3. 联网时设置的地址需要保存，未联网保存没有意义，但是未联网时设置的地址需要在返回是能看见，故改变	静态变量而不改变文件.



  4. 网络或数据库得到天气信息后，要将第一条给todayItem，其余给recycleView

     ```
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
     ```

     

5. 在创建通知消息时，api26以上要加上这段代码

   ```
   private void createNotificationChannel() {
       // Create the NotificationChannel, but only on API 26+ because
       // the NotificationChannel class is new and not in the support library
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           //创建通知渠道实例（这三个参数是必须要有的）
           NotificationChannel channel = new NotificationChannel("weatherForecast", "天气预报", NotificationManager.IMPORTANCE_HIGH);
           //创建通知渠道的通知管理器
           NotificationManager manager = (NotificationManager)
           getSystemService(NotificationManager.class);
           //将实例交给管理器
           manager.createNotificationChannel(channel);
       }
   }
   ```

   并提供channelId

   ```
   Notification notification = new NotificationCompat.Builder(MyApplication.getContext(), "weatherForecast")
   ```

   



6. 建立数据库表时，autoincrement只能用于int类型的属性，而我用在了string，导致建表失败，程序在查表时 崩溃，显示没有此表。



7. 联网、读写数据库等操作尽量不要在主程序中执行，应建立后台线程。如果主程序中数据库操作较多，可能会导致程序崩溃。



8. Android开发之APP安装后在桌面上不显示应用图标。

   ![image-20210102171319584](C:\Users\czc21\AppData\Roaming\Typora\typora-user-images\image-20210102171319584.png)