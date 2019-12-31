package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2019/12/27 0027.
 */
//GSON解析JSON数据
public class Basic {//某些JSON字段不适合直接作为Java字段命名，使用@SerializedName注解来使得JSON字段和Java字段之间建立联系
    @SerializedName("city")
    public String cityName;//eg:cityName与basic里的name有冲突，无法建立映射

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
