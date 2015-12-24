package com.example.zyt.weather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.example.zyt.weather.R;
import com.example.zyt.weather.model.Weather;
import com.example.zyt.weather.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WeatherActivity extends AppCompatActivity {

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDescText;
    private TextView lowTemp;
    private TextView highTemp;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.county_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDescText = (TextView) findViewById(R.id.weather_desp);
        lowTemp = (TextView) findViewById(R.id.temp1);
        highTemp = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);

        switchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
            }
        });

        refreshWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishText.setText("同步中……");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                        (WeatherActivity.this);
                String cityName = prefs.getString("city_name", "");
                if (cityName != null || cityName.length() > 0) {
                    queryWeatherInfo(cityName);
                }
            }
        });
        String countyName = getIntent().getStringExtra("county_name");
        if (countyName != null) {
            publishText.setText("同步中……");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(countyName);
        } else {
            showWeather();
        }
    }

    public void queryWeatherInfo(String countyName) {
        queryFromServer(countyName);
    }

    public void queryFromServer(String code) {
        String address = "http://apis.baidu.com/apistore/weatherservice/cityname";
        Parameters parameters = new Parameters();
        try {
            parameters.put("cityname", URLEncoder.encode(code, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiStoreSDK.execute(address, ApiStoreSDK.GET, parameters, new ApiCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                Utility.handleWeatherResponse(WeatherActivity.this, s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(int i, String s, Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败！");
                    }
                });
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        });
    }

    public void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        lowTemp.setText(prefs.getString("low_temp", ""));
        highTemp.setText(prefs.getString("high_temp", ""));
        weatherDescText.setText(prefs.getString("weather_desc", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
}
