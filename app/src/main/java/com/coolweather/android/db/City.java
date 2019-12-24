package com.coolweather.android.db;

        import org.litepal.LitePal;
        import org.litepal.crud.LitePalSupport;
/**
 * Created by Administrator on 2019/12/24 0024.
 */
public class City extends LitePalSupport {//低版本使用DataSupport
    private int id;//数据库用的id字段
    private String cityName;
    private int cityCode;//市的代号
    private int provinceId;//定位上一级省

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }
    public int geProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId =provinceId;
    }
}
