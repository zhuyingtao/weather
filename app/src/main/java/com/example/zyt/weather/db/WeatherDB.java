package com.example.zyt.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.zyt.weather.model.City;
import com.example.zyt.weather.model.County;
import com.example.zyt.weather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyt on 15/12/21 22:31.
 */
public class WeatherDB {
    public static final String DB_NAME = "weather";
    public static final int VERSION = 1;
    private static WeatherDB weatherDB;
    private SQLiteDatabase db;

    private WeatherDB(Context context) {
        WeatherOpenHelper dbHelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static WeatherDB getInstance(Context context) {
        if (weatherDB == null) {
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getName());
            values.put("province_code", province.getCode());
            db.insert("Province", null, values);
        }
    }

    public List<Province> loadProvinces() {
        List<Province> provinces = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinces.add(province);
            } while (cursor.moveToNext());
        }
        return provinces;
    }

    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getName());
            values.put("city_code", city.getCode());
            values.put("province_code", city.getProvinceCode());
            db.insert("City", null, values);
        }
    }

    public List<City> loadCities(String provinceCode) {
        List<City> cities = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_code = ?", new String[]{provinceCode}
                , null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceCode(provinceCode);
                cities.add(city);
            } while (cursor.moveToNext());
        }
        return cities;
    }

    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getName());
            values.put("county_code", county.getCode());
            values.put("city_code", county.getCityCode());
            db.insert("County", null, values);
        }
    }

    public List<County> loadCounties(String cityCode) {
        List<County> counties = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_code = ?", new String[]{cityCode}
                , null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityCode(cityCode);
                counties.add(county);
            } while (cursor.moveToNext());
        }
        return counties;
    }
}
