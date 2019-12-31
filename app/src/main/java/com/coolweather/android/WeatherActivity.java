package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;

public class WeatherActivity extends AppCompatActivity {

    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;//用于记录城市的天气id

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    private ImageView bingPicImg;//设置背景图片

    public DrawerLayout drawerLayout;
    public Button navButton;

//获取控件实例
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){//版本号21以上，也就是5.0以上系统才会执行后面的代码，用以融合背景图和状态栏
            View decorView =getWindow().getDecorView();//拿到当前活动的DecorView
            //改变系统UI的显示，两个参数表示活动的布局会显示在状态栏上面
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);//将状态栏设置为透明色
        }
        setContentView(R.layout.activity_weather);

        //初始化各控件
        drawerLayout =(DrawerLayout) findViewById(R.id.drawer_layout);
        navButton =(Button) findViewById(R.id.nav_button);

        bingPicImg =(ImageView) findViewById(R.id.bing_pic_img);

        weatherLayout=(ScrollView) findViewById(R.id.weather_layout);
        titleCity=(TextView) findViewById(R.id.title_city);
        titleUpdateTime=(TextView) findViewById(R.id.title_update_time);;
        degreeText=(TextView) findViewById(R.id.degree_text);;
        weatherInfoText=(TextView) findViewById(R.id.weather_info_text);;
        forecastLayout=(LinearLayout) findViewById(R.id.forecast_layout);;
        aqiText=(TextView) findViewById(R.id.aqi_text);
        pm25Text=(TextView) findViewById(R.id.pm25_text);
        comfortText=(TextView) findViewById(R.id.comfort_text);
        carWashText=(TextView) findViewById(R.id.car_wash_text);
        sportText=(TextView) findViewById(R.id.sport_text);

        //滑动菜单的逻辑处理
        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //添加SwipeRefreshLayout实例再设置下拉刷新进度条的颜色
        swipeRefresh =(SwipeRefreshLayout) findViewById(R.id.swip_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString =prefs.getString("weather",null);
        if(weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId =weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            mWeatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);//隐藏ScrollView
            requestWeather(mWeatherId);//去服务器请求天气数据
        }
        //设置一个下拉刷新的监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){//当触发了下拉刷新操作后，回调该方法
                requestWeather(mWeatherId);//请求天气信息
            }
        });


        //获取实例时尝试从SharePreferences中读取缓存背景图
        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic !=null){//若有缓存直接使用Glide加载图片
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{//没有则调用loadBingPic去请求今日的必应背景图
            loadBingPic();
        }
    }
    //根据天气ID请求城市天气信息
    public void requestWeather(final String weatherId){
        String weatherUrl ="http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl,new Callback(){
            @Override
        public void onResponse(Call call, Response response) throws IOException {
                final String responseText =response.body().string();
                final Weather weather =Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable(){//切换到主线程
                    @Override
                public void run(){
                        if(weather!=null&&"ok".equals(weather.status)){//数据缓存
                            SharedPreferences.Editor editor =PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);//用于表示刷新事件结束，并隐藏刷新进度条
                    }
                });
            }
            @Override
        public void onFailure(Call call,IOException e){
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);//用于表示刷新事件结束，并隐藏刷新进度条
                    }
                });
            }
        });
        loadBingPic();//每次请求的时候会同时刷新背景图片
    }
    //加载必应每日一图
    private void loadBingPic(){
        String requestBingPic ="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
        //获取必应背景图的链接，再存于SharePreferences里，再将当前线程切换到主线程，最后用Glide加载图片
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }


        });
    }
    //处理并展示Weather实体类中的数据
    private void showWeatherInfo(Weather weather){//从Weather对象中获取数据显示到控件上
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
//循环中动态加载forecast_item
        for (Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText =(TextView) view.findViewById(R.id.date_text);
            TextView infoText =(TextView) view.findViewById(R.id.info_text);
            TextView  maxText=(TextView) view.findViewById(R.id.max_text);
            TextView  minText=(TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度:"+weather.suggestion.comfort.info;
        String carwash="洗车指数:"+weather.suggestion.comfort.info;
        String sport="运动建议:"+weather.suggestion.sport.info;

        comfortText.setText(comfort);
        carWashText.setText(carwash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        //激活AutoUpdateService服务
        Intent intent =new Intent(this, AutoUpdateService.class);
        startService(intent);
    }



}
