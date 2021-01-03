package com.example.weatherforecast;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.database.sqlite.SQLiteDatabase;

public class WeatherFetcher {
    private static final String TAG="WeatherFetcher";
    private static final String API_KEY="b8a4c557b986411db26d1208bd215441";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url=new URL(urlSpec);
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        connection.setInstanceFollowRedirects(false);
        String redirect=connection.getHeaderField("Location");
        if(redirect!=null){
            connection=(HttpURLConnection)new URL(redirect).openConnection();
        }

        try{
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            InputStream in=connection.getInputStream();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage()+": with"+urlSpec);
            }
            int bytesRead=0;
            byte[] buffer=new byte[1024];
            while((bytesRead=in.read(buffer))>0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems(String locationName){
        List<GalleryItem> items=new ArrayList<>();
        try{
            String url=Uri.parse("https://geoapi.qweather.com/v2/city/lookup/")
                    .buildUpon()
                    .appendQueryParameter("location",locationName)
                    .appendQueryParameter("key",API_KEY)
                    .build().toString();
            String jsonString=getUrlString(url);
            JSONObject jsonObject=new JSONObject(jsonString);
            JSONArray jsonArray=jsonObject.getJSONArray("location");
            if(jsonArray.length()>0){
                JSONObject locationJsonObject = jsonArray.getJSONObject(0);
                Location.sLocationId=locationJsonObject.getString("id");
                Location.sLocationName=locationJsonObject.getString("name");
                Location.sLon=locationJsonObject.getString("lon");
                Location.sLat=locationJsonObject.getString("lat");
            }
            Log.i(TAG,"Received JSON: "+jsonString);
        }catch (IOException ioe){
            Log.i(TAG,"Failed to fetch items",ioe);
            ioe.printStackTrace();
        }catch (JSONException je){
            Log.e(TAG,"Failed to parse JSON",je);
            je.printStackTrace();
        }
        try{
            String url= Uri.parse("https://devapi.qweather.com/v7/weather/15d/")
                    .buildUpon()
                    .appendQueryParameter("location",Location.sLocationId)
                    .appendQueryParameter("key",API_KEY)
                    .build().toString();
            String jsonString=getUrlString(url);
            JSONObject jsonObject=new JSONObject(jsonString);
            parseItems(items,jsonObject);
            Log.i(TAG,"Received JSON: "+jsonString);
        }catch (IOException ioe){
            Log.e(TAG,"Failed to fetch items",ioe);
            ioe.printStackTrace();
        }catch (JSONException je){
            Log.e(TAG,"Failed to parse JSON",je);
            je.printStackTrace();
        }
        return items;
    }

    public void parseItems(List<GalleryItem> items, JSONObject jsonObject) throws IOException, JSONException {
        JSONArray weatherJsonArray = jsonObject.getJSONArray("daily");
        for(int i=0;i<weatherJsonArray.length();i++){
            JSONObject weatherJsonObject = weatherJsonArray.getJSONObject(i);

            GalleryItem item=new GalleryItem();
            item.setData(weatherJsonObject.getString("fxDate"));
            item.setTempMax(weatherJsonObject.getString("tempMax"));
            item.setTempMin(weatherJsonObject.getString("tempMin"));
            item.setTextDay(weatherJsonObject.getString("textDay"));
            item.setIconDay(weatherJsonObject.getString("iconDay"));
            item.setHumidity(weatherJsonObject.getString("humidity"));
            item.setPressure(weatherJsonObject.getString("pressure"));
            item.setWindSpeedDay(weatherJsonObject.getString("windSpeedDay"));
            item.setTempType("C");

            items.add(item);
        }
        WeatherLab mlab=new WeatherLab();
        mlab.updateWeather(items);
        WeatherLab.sWeathers=items;
    }
}
