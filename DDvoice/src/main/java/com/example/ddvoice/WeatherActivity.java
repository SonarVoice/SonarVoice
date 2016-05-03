package com.example.ddvoice;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;


import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by owen_ on 2016-04-18.
 */
public class WeatherActivity extends Activity{

    Button weatherReturnButton,weatherUpdateButton,readButton;
    public TextView testTextView;
    private LocationService locationService;
    String weatherJsonStr;

    static String testStr,nowLocation = null;
    static int locationServiceStatus = 0;

    Map<String,String> nowWeatherInfo = new HashMap<String,String>();

    WeatherUtils weatherUtils = new WeatherUtils();


    public TextView weatherStatus;
    public TextView temperature;
    public TextView date;
    public TextView PM25;
    public TextView location;
    public ImageView todayActWeatherIcon;

    public ImageView todayWeatherIcon;
    public TextView todayWeather;
    public TextView todayTemperature;

    public ImageView tomorrowWeatherIcon;
    public TextView tomorrowWeather;
    public TextView tomorrowTemperature;

    public ImageView dayAfterTomorrowWeatherIcon;
    public TextView dayAfterTomorrowWeather;
    public TextView dayAfterTomorrowTemperature;

    public TextView liftSuggestionStatus;
    public TextView liftSuggestionText;
    public TextView CarWashingSuggestionStatus;
    public TextView CarWashingSuggestionText;
    public TextView clothesSuggestionStatus;
    public TextView clothesSuggestionText;
    public TextView fluSuggestionStatus;
    public TextView fluSuggestionText;
    public TextView sportsSuggestionStatus;
    public TextView sportsSuggestionText;
    public TextView travSuggestionStatus;
    public TextView travSuggestionText;
    public TextView ultravioletRaysSuggestionStatus;
    public TextView ultravioletRaysSuggestionText;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_main);
        addListener();
//        apiTest(nowLocation);
        testTextView = (TextView)findViewById(R.id.testTextView2);



    }



    void addListener()
    {
        weatherReturnButton = (Button)findViewById(R.id.weatherReturnButton);
        weatherReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent().setClass(WeatherActivity.this, MainActivity.class));
                WeatherActivity.this.finish();
            }
        });

        weatherUpdateButton = (Button)findViewById(R.id.weatherUpdateButton);
        weatherUpdateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                updateWeather();
                System.out.println("weatherUpdateButton");
            }
        });

        readButton = (Button)findViewById(R.id.weatherReadButton);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testTextView.setText("readButton");
            }
        });

    }

    public void updateWeather()
    {
//        if (locationServiceStatus == 0) {
//            locationService.start();// 定位SDK
//            // start之后会默认发起一次定位请求，�?��者无须判断isstart并主动调用request
//            weatherUpdateButton.setText("更新");
//            locationServiceStatus = 1;
//        } else {
//            locationService.stop();
//            weatherUpdateButton.setText("更新完毕");
//            locationServiceStatus = 0;
//        }
        locationService.start();
        locationService.stop();
    }


    //调用天气api服务
    public void apiTest(String city) {

        if(city != null)
        {
            Parameters para = new Parameters();

            para.put("city", city);
            ApiStoreSDK.execute("http://apis.baidu.com/heweather/weather/free",
                    ApiStoreSDK.GET,
                    para,
                    new ApiCallBack() {
                        @Override
                        public void onSuccess(int status, String responseString) {
                            Log.i("sdkdemo", "onSuccess");

                            setWeatherUI(weatherUtils.getNowWeatherInJson(responseString));
                        }

                        @Override
                        public void onComplete() {
                            Log.i("sdkdemo", "onComplete");
                        }

                        @Override
                        public void onError(int status, String responseString, Exception e) {
                            Log.i("sdkdemo", "onError, status: " + status);
                            Log.i("sdkdemo", "errMsg: " + (e == null ? "" : e.getMessage()));
                            weatherJsonStr = getStackTrace(e);
                        }
                    });
            nowLocation = null;
        }


    }

    String getStackTrace(Throwable e) {
        if (e == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        str.append(e.getMessage()).append("\n");
        for (int i = 0; i < e.getStackTrace().length; i++) {
            str.append(e.getStackTrace()[i]).append("\n");
        }
        return str.toString();
    }

    //定位部分
    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监�?
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
        locationService = ((MyApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参�?其他示例的activity，都是�?过此种方式获取locationservice实例�?
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }

    }


    /*****
     * @see copy funtion to you project
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修�?
     *
     */
    public BDLocationListener mListener = new BDLocationListener() {

        String locationStr;

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub

            apiTest(weatherUtils.switchCity(location.getCity().toString()));


        }
    };

    public void setWeatherUI( Map<String,Map<String,String>> WeatherInfo) {



        Map<String,String> todayActualWeatherInfo = new HashMap<>();
        Map<String,String> todayForecastWeather = new HashMap<>();
        Map<String,String> tomorrowForecastWeather = new HashMap<>();
        Map<String,String> dayAfterTomorrowForecastWeather = new HashMap<>();
        Map<String,String> weatherSuggestion = new HashMap<>();

        todayActualWeatherInfo = WeatherInfo.get("todayActualWeatherInfo");
        todayForecastWeather = WeatherInfo.get("todayForecastWeather");
        tomorrowForecastWeather = WeatherInfo.get("tomorrowForecastWeather");
        dayAfterTomorrowForecastWeather = WeatherInfo.get("dayAfterTomorrowForecastWeather");
        weatherSuggestion = WeatherInfo.get("weatherSuggestion");

        System.out.println(todayActualWeatherInfo);

        weatherStatus = (TextView)findViewById(R.id.weatherStatus);
        temperature = (TextView)findViewById(R.id.temperature);
        date = (TextView)findViewById(R.id.date);
        PM25 = (TextView)findViewById(R.id.PM25);
        location = (TextView)findViewById(R.id.location);
        todayActWeatherIcon = (ImageView)findViewById(R.id.todayActWeatherIcon);

        todayWeatherIcon = (ImageView)findViewById(R.id.todayWeatherIcon);
        todayWeather = (TextView)findViewById(R.id.todayWeather);
        todayTemperature = (TextView)findViewById(R.id.toadyTemperature);

        tomorrowWeatherIcon = (ImageView)findViewById(R.id.tomorrowWeatherIcon);
        tomorrowWeather = (TextView)findViewById(R.id.tomorrowWeather);
        tomorrowTemperature = (TextView)findViewById(R.id.tomorrowTemperature);

        dayAfterTomorrowWeatherIcon = (ImageView)findViewById(R.id.dayAfterTomorrowWeatherIcon);
        dayAfterTomorrowWeather = (TextView)findViewById(R.id.dayAfterTomorrowWeather);
        dayAfterTomorrowTemperature = (TextView)findViewById(R.id.dayAfterTomorrowTemperature);

        liftSuggestionStatus = (TextView)findViewById(R.id.liftSuggestionStatus);
        liftSuggestionText = (TextView)findViewById(R.id.liftSuggestionText);
        CarWashingSuggestionStatus = (TextView)findViewById(R.id.CarWashingSuggestionStatus);
        CarWashingSuggestionText = (TextView)findViewById(R.id.CarWashingSuggestionText);
        clothesSuggestionStatus = (TextView)findViewById(R.id.clothesSuggestionStatus);
        clothesSuggestionText = (TextView)findViewById(R.id.clothesSuggestionText);
        fluSuggestionStatus = (TextView)findViewById(R.id.fluSuggestionStatus);
        fluSuggestionText = (TextView)findViewById(R.id.fluSuggestionText);
        sportsSuggestionStatus = (TextView)findViewById(R.id.sportsSuggestionStatus);
        sportsSuggestionText = (TextView)findViewById(R.id.sportsSuggestionText);
        travSuggestionStatus = (TextView)findViewById(R.id.travSuggestionStatus);
        travSuggestionText = (TextView)findViewById(R.id.travSuggestionText);
        ultravioletRaysSuggestionStatus = (TextView)findViewById(R.id.ultravioletRaysSuggestionStatus);
        ultravioletRaysSuggestionText = (TextView)findViewById(R.id.ultravioletRaysSuggestionText);

        TextView updateTime = (TextView)findViewById(R.id.weatherUpdateTime);

        //今日天气详情
        weatherStatus.setText(todayActualWeatherInfo.get("txt"));
        temperature.setText(todayActualWeatherInfo.get("tmp") + "��");
                date.setText("���գ�" + todayActualWeatherInfo.get("todayDate"));
                        PM25.setText("PM2.5 : " + todayActualWeatherInfo.get("pm25") + " " + todayActualWeatherInfo.get("qlty"));
        location.setText(todayActualWeatherInfo.get("city") + "��");
        todayActWeatherIcon.setImageResource(getResources().getIdentifier("weather" + todayActualWeatherInfo.get("code"), "drawable", "com.example.owen_.speechrecognitionver1"));

        //今日预计天气情况
        todayWeatherIcon.setImageResource(getResources().getIdentifier("weather" + todayForecastWeather.get("code"), "drawable", "com.example.owen_.speechrecognitionver1"));
        todayWeather.setText(todayForecastWeather.get("txt"));
        todayTemperature.setText(todayForecastWeather.get("minTmp") + "��/"+todayForecastWeather.get("maxTmp") + "��");

        //明日预计天气情况
        tomorrowWeatherIcon.setImageResource(getResources().getIdentifier("weather" + tomorrowForecastWeather.get("code"), "drawable", "com.example.owen_.speechrecognitionver1"));
        tomorrowWeather.setText(tomorrowForecastWeather.get("txt"));
        tomorrowTemperature.setText(tomorrowForecastWeather.get("minTmp") + "��/"+tomorrowForecastWeather.get("maxTmp") + "��");

        //后日预计天气情况
        dayAfterTomorrowWeatherIcon.setImageResource(getResources().getIdentifier("weather" + dayAfterTomorrowForecastWeather.get("code"), "drawable", "com.example.owen_.speechrecognitionver1"));
        dayAfterTomorrowWeather.setText(dayAfterTomorrowForecastWeather.get("txt"));
        dayAfterTomorrowTemperature.setText(dayAfterTomorrowForecastWeather.get("minTmp") + "��/"+dayAfterTomorrowForecastWeather.get("maxTmp") + "��");

        //生活指数建议
        liftSuggestionStatus.setText(weatherSuggestion.get("comfBrf"));
        liftSuggestionText.setText(weatherSuggestion.get("comfTxt"));
        CarWashingSuggestionStatus.setText(weatherSuggestion.get("cwBrf"));
        CarWashingSuggestionText.setText(weatherSuggestion.get("cwTxt"));
        clothesSuggestionStatus.setText(weatherSuggestion.get("drsgBrf"));
        clothesSuggestionText.setText(weatherSuggestion.get("drsgTxt"));
        fluSuggestionStatus.setText(weatherSuggestion.get("fluBrf"));
        fluSuggestionText.setText(weatherSuggestion.get("fluTxt"));
        sportsSuggestionStatus.setText(weatherSuggestion.get("sportBrf"));
        sportsSuggestionText.setText(weatherSuggestion.get("sportTxt"));
        travSuggestionStatus.setText(weatherSuggestion.get("travBrf"));
        travSuggestionText.setText(weatherSuggestion.get("travTxt"));
        ultravioletRaysSuggestionStatus.setText(weatherSuggestion.get("uvBrf"));
        ultravioletRaysSuggestionText.setText(weatherSuggestion.get("uvTxt"));

        updateTime.setText(todayActualWeatherInfo.get("updateTime"));


        testTextView = (TextView)findViewById(R.id.testTextView2);
        testTextView.setText("���");


    }






//    TextView updateTime = (TextView)findViewById(R.id.weatherUpdateTime);
}
