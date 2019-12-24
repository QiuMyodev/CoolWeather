package com.coolweather.android.db;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;
/**
 * Created by Administrator on 2019/12/24 0024.
 */
public class County extends LitePalSupport {//低版本使用DataSupport
    private int id;//数据库用的id字段
    private String countyName;
    private String weatherId;//对于县天气id
    private int cityId;//定位上一级市

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
    public int geCityId() {
        return cityId;
    }

    public void seCityId(int cityId) {
        this.cityId =cityId;
    }
}
