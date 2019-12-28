package com.coolweather.android.gson;

/**
 * Created by Administrator on 2019/12/27 0027.
 */
public class AQI {
    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
