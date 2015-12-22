package com.example.zyt.weather.util;

import android.text.TextUtils;

import com.example.zyt.weather.db.WeatherDB;
import com.example.zyt.weather.model.City;
import com.example.zyt.weather.model.County;
import com.example.zyt.weather.model.Province;

/**
 * Created by zyt on 15/12/22 10:20.
 */
public class Utility {

    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] provinces = response.split(",");
            if (provinces != null && provinces.length > 0) {
                for (String p : provinces) {
                    String[] parts = p.split("\\|");
                    Province province = new Province();
                    province.setCode(parts[0]);
                    province.setName(parts[1]);
                    weatherDB.saveProvince(province);
                }
            }
            return true;
        }
        return false;
    }

    public synchronized static boolean handleCitiesResponse(WeatherDB weatherDB, String response,
                                                            int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] cities = response.split(",");
            if (cities != null && cities.length > 0) {
                for (String c : cities) {
                    String[] parts = c.split("\\|");
                    City city = new City();
                    city.setCode(parts[0]);
                    city.setName(parts[1]);
                    city.setProvinceId(provinceId);
                    weatherDB.saveCity(city);
                }
            }
            return true;
        }
        return false;
    }

    public synchronized static boolean handleCountiesResponse(WeatherDB weatherDB, String response,
                                                              int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] counties = response.split(",");
            if (counties != null && counties.length > 0) {
                for (String c : counties) {
                    String[] parts = c.split("\\|");
                    County county = new County();
                    county.setCode(parts[0]);
                    county.setName(parts[1]);
                    county.setCityId(cityId);
                    weatherDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }
}
