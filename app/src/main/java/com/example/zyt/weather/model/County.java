package com.example.zyt.weather.model;

/**
 * Created by zyt on 15/12/21 22:27.
 */
public class County {
    private int id;
    private String name;
    private String code;
    private String cityName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public boolean equals(Object o) {
        return this.name.equals(((County) o).name);
    }

    @Override
    public String toString() {
        return this.name + " " + this.code;
    }
}
