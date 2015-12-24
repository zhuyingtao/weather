package com.example.zyt.weather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.example.zyt.weather.R;
import com.example.zyt.weather.db.WeatherDB;
import com.example.zyt.weather.model.City;
import com.example.zyt.weather.model.County;
import com.example.zyt.weather.model.Province;
import com.example.zyt.weather.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private int currentLevel;


    private TextView titleText;
    private ListView listView;
    private WeatherDB weatherDB;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    private ProgressDialog progressDialog;

    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    selectedCounty = countyList.get(position);
                    queryWeather();
                }
            }
        });
        weatherDB = WeatherDB.getInstance(this);
        queryProvinces();
    }

    private void queryProvinces() {
        provinceList = weatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province p : provinceList) {
                dataList.add(p.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    private void queryCities() {
        cityList = weatherDB.loadCities(selectedProvince.getName());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City c : cityList) {
                dataList.add(c.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getName(), "city");
        }
    }

    private int times = 0;

    private void queryCounties() {
        countyList = weatherDB.loadCounties(selectedCity.getName());
        if (countyList.size() > 0 || times >= 1) {
            dataList.clear();
            for (County c : countyList) {
                dataList.add(c.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getName());
            currentLevel = LEVEL_COUNTY;
            times = 0;
        } else {
            times++;  //the data from server may have some problems;
            queryFromServer(selectedCity.getName(), "county");
        }
    }

    private void queryWeather() {
        String countyName = selectedCounty.getName();
        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra("county_name", countyName);
        startActivity(intent);
        finish();
    }

    private void queryFromServer(final String code, final String type) {
        showProgressDialog();
        if ("province".equals(type)) {
            Utility.handleProvincesResponse(weatherDB);
            queryProvinces();
            closeProgressDialog();
        } else {
            String address = "http://apis.baidu.com/apistore/weatherservice/citylist";
            Parameters parameters = new Parameters();
            try {
                parameters.put("cityname", URLEncoder.encode(code, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ApiStoreSDK.execute(address, ApiStoreSDK.GET, parameters, new ApiCallBack() {
                boolean result = false;

                @Override
                public void onSuccess(int i, String s) {
                    if (type.equals("city")) {
                        result = Utility.handleCitiesResponse(weatherDB, s, selectedProvince
                                .getName());
                    } else if (type.equals("county")) {
                        result = Utility.handleCountiesResponse(weatherDB, s, selectedCity
                                .getName());
                    }

                    if (result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                if (type.equals("city")) {
                                    queryCities();
                                } else if (type.equals("county")) {
                                    queryCounties();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(int i, String s, Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }

                @Override
                public void onComplete() {
                    super.onComplete();
                }
            });
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载……");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
