package com.example.zyt.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.zyt.weather.db.WeatherDB;
import com.example.zyt.weather.model.City;
import com.example.zyt.weather.model.County;
import com.example.zyt.weather.model.Province;
import com.example.zyt.weather.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zyt on 15/12/22 10:20.
 */
public class Utility {

    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB) {
        String[][] provinces = getProvinces();
        for (String[] p : provinces) {
            Province province = new Province();
            province.setCode(p[0]);
            province.setName(p[1]);
            weatherDB.saveProvince(province);
        }
        return true;
    }

    //{
    //        "province_cn": "河南",
    //        "district_cn": "郑州",
    //        "name_cn": "郑州",
    //        "name_en": "zhengzhou",
    //        "area_id": "101180101"
    //}
    public synchronized static boolean handleCitiesResponse(WeatherDB weatherDB, String response,
                                                            String provinceName) {
        List<City> cities = new ArrayList<>();
        try {
            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray jsonArray = jsonObject.getJSONArray("retData");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                // provinceName must be matched;
                if (provinceName.equals(object.getString("province_cn"))) {
                    City city = new City();
                    city.setName(object.getString("district_cn"));
                    city.setCode(object.getString("area_id").substring(3, 7));
                    city.setProvinceName(provinceName);
                    // only store the unrepeated city;
                    if (!cities.contains(city)) {
                        cities.add(city);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("city" + provinceName, cities.toString());
        if (cities.size() > 0) {
            for (City c : cities) {
                weatherDB.saveCity(c);
            }
        }
        return true;
    }

    //{
    //        "province_cn": "山东",
    //        "district_cn": "济南",
    //        "name_cn": "长清",
    //        "name_en": "changqing",
    //        "area_id": "101120102"
    //}
    public synchronized static boolean handleCountiesResponse(WeatherDB weatherDB, String response,
                                                              String cityName) {
        List<County> counties = new ArrayList<>();
        try {
            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray jsonArray = jsonObject.getJSONArray("retData");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                // cityId must be matched;
                if (cityName.equals(object.getString("district_cn"))) {
                    County county = new County();
                    county.setName(object.getString("name_cn"));
                    county.setCode(object.getString("area_id").substring(3));
                    county.setCityName(cityName);
                    // only store the unrepeated county;
                    if (!counties.contains(county)) {
                        counties.add(county);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("county" + cityName, counties.toString());
        if (counties.size() > 0) {
            for (County c : counties) {
                weatherDB.saveCounty(c);
            }
        }
        return true;
    }

    // store all the provinces to avoid query from sever.
    private static String[][] getProvinces() {
        String[][] provinces = {{"01", "北京"}, {"02", "上海"}, {"03", "天津"}, {"04", "重庆"}, {"05",
                "黑龙江"}, {"06", "吉林"}, {"07", "辽宁"}, {"08", "内蒙古"}, {"09", "河北"}, {"10", "山西"},
                {"11", "陕西"}, {"12", "山东"}, {"13", "新疆"}, {"14", "西藏"}, {"15", "青海"}, {"16",
                "甘肃"}, {"17", "宁夏"}, {"18", "河南"}, {"19", "江苏"}, {"20", "湖北"}, {"21", "浙江"},
                {"22", "安徽"}, {"23", "福建"}, {"24", "江西"}, {"25", "湖南"}, {"26", "贵州"}, {"27",
                "四川"}, {"28", "广东"}, {"29", "云南"}, {"30", "广西"}, {"31", "海南"}, {"32", "香港"},
                {"33", "澳门"}, {"34", "台湾"},};
        return provinces;
    }


    //{
    //        city:"北京", //城市
    //        pinyin:"beijing", //城市拼音
    //        citycode:"101010100",  //城市编码
    //        date:"15-02-11", //日期
    //        time:"11:00", //发布时间
    //        postCode:"100000", //邮编
    //        longitude:116.391, //经度
    //        latitude:39.904, //维度
    //        altitude:"33", //海拔
    //        weather:"晴",  //天气情况
    //        temp:"10", //气温
    //        l_tmp:"-4", //最低气温
    //        h_tmp:"10", //最高气温
    //        WD:"无持续风向",     //风向
    //        WS:"微风(<10m/h)", //风力
    //        sunrise:"07:12", //日出时间
    //        sunset:"17:44" //日落时间
    //}

    public static void handleWeatherResponse(Context context, String response) {
        Log.v("weather", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("retData");
            String cityName = weatherInfo.getString("city");
            String lowTemp = weatherInfo.getString("l_tmp");
            String highTemp = weatherInfo.getString("h_tmp");
            String weatherDesc = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("time");
            Weather weather = new Weather();
            weather.setCityName(cityName);
            weather.setLowTemp(Integer.parseInt(lowTemp));
            weather.setHighTemp(Integer.parseInt(highTemp));
            weather.setWeatherDesc(weatherDesc);
            weather.setPublishTime(publishTime);
            saveWeatherInfo(context, weather);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, Weather weather) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy 年M月d 日");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", weather.getCityName());
        editor.putInt("low_temp", weather.getLowTemp());
        editor.putInt("high_temp", weather.getHighTemp());
        editor.putString("weather_desc", weather.getWeatherDesc());
        editor.putString("publish_time", weather.getPublishTime());
        editor.putString("current_date", simpleDateFormat.format(new Date()));
        editor.commit();
    }
}
