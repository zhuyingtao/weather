package com.example.zyt.weather.util;

import android.util.Log;

import com.example.zyt.weather.db.WeatherDB;
import com.example.zyt.weather.model.City;
import com.example.zyt.weather.model.County;
import com.example.zyt.weather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
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
}
