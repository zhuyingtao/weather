package com.example.zyt.weather.util;

/**
 * Created by zyt on 15/12/22 00:39.
 */
public interface HttpCallBackListener {
    void onFinish(String response);

    void onError(Exception e);
}
