package com.example.zyt.weather.model;

/**
 * Created by zyt on 15/12/21 22:25.
 */
public class City {
    private int id;
    private String name;
    private String code;
    private String provinceCode;

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        return this.name.equals(((City) o).name);
    }

    @Override
    public String toString() {
        return this.name + " " + this.code;
    }
}
