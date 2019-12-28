package com.coolweather.android;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.db.City;
import com.coolweather.android.db.Province;
import com.coolweather.android.db.County;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
/**
 * Created by Administrator on 2019/12/27 0027.
 */
public class ChooseAreaFragment extends Fragment{
    //编写用于遍历省市县数据的碎片
    public static final int LEVEL_PROVINCE =0;
    public static final int LEVEL_CITY =1;
    public static final int LEVEL_COUNTY =2;

    private ProgressDialog progressDialog;

    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;
    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //选中的级别
    private int currentLevel;

    @Override
    //初始化控件实例
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.choose_area, container, false);
        titleText =(TextView) view.findViewById(R.id.title_text);
        backButton=(Button) view.findViewById(R.id.back_button);
        listView=(ListView) view.findViewById(R.id.list_view);//显示列表
//默认继承ArrayAdapter
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        return view;
    }
    @Override
    //设置点击事件
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>parent,View view ,int position,long id){
                if (currentLevel == LEVEL_PROVINCE) {//定位点击的省
                    selectedProvince = provinceList.get(position);
                    queryCities();//查找所有子城市
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel ==LEVEL_COUNTY){
                    String weatherId=countyList.get(position).getWeatherId();

                    if(getActivity() instanceof MainActivity) {//用于判断该碎片对象是否为主活动的一个实例
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity) {
                    //判断该碎片对象是否为天气活动的一个实例，若在此，则关闭滑动菜单，显示下拉进度条，然后请求新城市的天气信息
                        WeatherActivity activity=(WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);

                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    //查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);//全部取出，放入provinceList

        if(provinceList.size()>0){
            dataList.clear();
            for(Province province :provinceList){
                dataList.add(province.getProvinceName());//遍历数组，按名称放入dataList
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);//默认选中第0个
            currentLevel=LEVEL_PROVINCE;
        }else{
            String address ="http//guolin.tech/api/china";
            queryFromSever(address,"province");
        }
    }
    //查询全国所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);//设置可见性
        //在数据库中查找被选中的城市的id
        cityList =DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city :cityList){
                dataList.add(city.getCityName());//按名称放入dataList
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            int provinceCode =selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromSever(address,"city");
        }
    }
    //查询全国所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);//设置可见性
        //在数据库中查找被选中的城市的id
        countyList =DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county :countyList){
                dataList.add(county.getCountyName());//按名称放入dataList
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            int provinceCode =selectedProvince.getProvinceCode();
            int cityCode =selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromSever(address,"county");
        }
    }
    //根据传入的地址和类型从服务器上查询省市县数据
    private  void queryFromSever(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException{
                String responseText=response.body().string();//响应数据
                boolean result =false;//还不知道能不能解析
                if("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);//解析处理数据
                }else if("city".equals(type)){
                    result= Utility.handleCityResponse(responseText,selectedProvince.getId());//解析处理数据
                }else if("county".equals(type)){
                    result= Utility.handleCountyResponse(responseText,selectedCity.getId());//解析处理数据
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            closeProgressDialog();//进度条关闭
                            //重新执行（从数据库中取表，表内数据显示再listView上
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call,IOException e){
                //通过runOnUiThread（）方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    //现实进度对话框
    private void showProgressDialog(){
        if(progressDialog ==null){
            progressDialog =new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    //关闭进度对话框
    private void closeProgressDialog(){
        if(progressDialog !=null){
            progressDialog.dismiss();
        }
    }
}


