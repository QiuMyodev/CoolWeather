package com.coolweather.android.db;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

/**
 * Created by Administrator on 2019/12/24 0024.
 */
public class Province extends LitePalSupport{//低版本使用DataSupport
    private int id;//数据库用的id字段
    private String provinceName;
    private int provinceCode;//省的代号
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getProvinceName(){
        return provinceName;
    }
    public void setProvinceName(String provinceName){
        this.provinceName=provinceName;
    }
    public int getProvinceCode(){
        return provinceCode;
    }
    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
