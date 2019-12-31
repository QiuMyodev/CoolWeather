package com.coolweather.android.util;

import android.app.VoiceInteractor;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/12/24 0024.
 */
//和服务器进行交互，获取全国省县市的数据
public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        //传入请求地址，注册一个回调来处理服务器响应
        OkHttpClient client =new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

}
