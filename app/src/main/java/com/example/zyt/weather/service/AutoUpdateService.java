package com.example.zyt.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.example.zyt.weather.receiver.AutoUpdateReceiver;
import com.example.zyt.weather.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + time;
        Intent intent1 = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName = preferences.getString("city_name", "");
        String address = "http://apis.baidu.com/apistore/weatherservice/cityname";
        Parameters parameters = new Parameters();
        try {
            parameters.put("cityname", URLEncoder.encode(cityName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiStoreSDK.execute(address, ApiStoreSDK.GET, parameters, new ApiCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                Utility.handleWeatherResponse(AutoUpdateService.this, s);
            }

            @Override
            public void onError(int i, String s, Exception e) {
                super.onError(i, s, e);
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
