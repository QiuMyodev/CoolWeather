package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2019/12/27 0027.
 */
public class Weather {//总的实例类，用于引用basic aqi suggestion daily_forecast对应的实例类
    public String status;
    public Basic bastic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
