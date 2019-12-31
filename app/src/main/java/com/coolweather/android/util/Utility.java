package com.coolweather.android.util;
//import org.litepal.LitePal;
import android.text.TextUtils;

import com.coolweather.android.db.Province;
import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2019/12/24 0024.
 */

//解析和处理服务器返回的省级数据（json格式）
// 先使用JSONArray和JSONObject将数据解析出来，然后组装成实体类对象，再把数据写入数据库中
public class Utility {

    public static boolean handleProvinceResponse(String response) {
        //解析处理省级数据
        if (!TextUtils.isEmpty(response)) {
            try{
                JSONArray allProvinces =new JSONArray(response);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject =allProvinces.getJSONObject(i);
                    //写入数据库
                    Province province=new Province();//创建对象
                    province.setProvinceName(provinceObject.getString("name"));//写入数据
                    province.setProvinceCode(provinceObject.getInt("id"));//写入数据
                    province.save();//把数据保存数据库中
                }
                return true;//抓取成功
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;//抓取失败
    }
    //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)) {
            try{
                JSONArray allCities =new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject cityObject =allCities.getJSONObject(i);
                    //写入数据库
                    City city=new City();//创建对象
                    city.setCityName(cityObject.getString("name"));//写入数据
                    city.setCityCode(cityObject.getInt("id"));//写入数据
                    city.setProvinceId(provinceId);
                    city.save();//把数据写入数据库中
                }
                return true;//抓取成功
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;//抓取失败
    }
    //解析和处理服务器返回的县级数据
    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)) {
            try{
                JSONArray allCounties =new JSONArray(response);
                for(int i=0;i<allCounties.length();i++){
                    JSONObject countyObject =allCounties.getJSONObject(i);
                    //写入数据库
                    County county=new County();//创建对象
                    county.setCountyName(countyObject.getString("name"));//写入数据
                    county.setWeatherId(countyObject.getString("weather_id"));//写入数据
                    county.setCityId(cityId);
                    county.save();//把数据写入数据库中
                }
                return true;//抓取成功
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;//抓取失败
    }

    //将返回的天气JSON数据解析成Weather实体类

    public static Weather handleWeatherResponse(String resopnse){
        try{
            JSONObject jsonObject=new JSONObject(resopnse);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
