package com.example.a17289.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.example.a17289.bean.TodayWeather;
import com.example.a17289.gson.Weather;
import com.example.a17289.util.NetUtil;
import com.google.gson.Gson;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import java.util.logging.SimpleFormatter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// dp * ppi / 160 = px
// mdpi -> 160
// hdpi -> 240
// xxdpi -> 320
// xxdpi -> 480

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // 用于定位
    private LocationClient mLocationClient;
    private EditText textUser;
    private EditText textPass;
    private String currentCityCode = "101010800";
    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int UPDATE_XMLTODAY_WEATHER = 2;
    private static final String WEATHER_BAOXUE = "暴雪";
    private static final String WEATHER_BAOYU = "暴雨";
    private static final String WEATHER_DABAOYU = "大暴雨";
    private static final String WEATHER_DAXUE = "大雪";
    private static final String WEATHER_DAYU = "大雨";
    private static final String WEATHER_DUOYUN = "多云";
    private static final String WEATHER_LEIZHENYU = "雷阵雨";
    private static final String WEATHER_LEIZHENYUBINGBAO = "雷阵雨冰雹";
    private static final String WEATHER_QING = "晴";
    private static final String WEATHER_SHACHENBAO = "沙尘暴";
    private static final String WEATHER_TEDABAOYU = "特大暴雨";
    private static final String WEATHER_WU = "乌";
    private static final String WEATHER_XIAOXUE = "小雪";
    private static final String WEATHER_XIAOYU = "小雨";
    private static final String WEATHER_YIN = "阴";
    private static final String WEATHER_YUJIAXUE = "雨夹雪";
    private static final String WEATHER_ZHENXUE = "阵雪";
    private static final String WEATHER_ZHENYU = "阵雨";
    private static final String WEATHER_ZHONGXUE = "中雪";
    private static final String WEATHER_ZHONGYU = "中雨";

    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private Button btn;
    private Handler handler;
    private TextView cityTv,
                     timeTv,
                     humidityTv,
                     weekTv,
                     pmDataTv,
                     pmQualityTv,
                     temperatureTv,
                     climateTv,
                     windTv,
                     city_name_Tv,
                    current_temperature;
    private ImageView weatherImg, pmImg;

    // 通过消息机制来更新UI界面的数据
    // 主线程将接收消息
    class InnerHandler extends Handler{
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((Weather) msg.obj);
                    break;
                case UPDATE_XMLTODAY_WEATHER:
                    updateXMLTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    // 初始化
    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        current_temperature = (TextView) findViewById(R.id.current_temperature);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        weekTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        handler = new InnerHandler();
        queryWeatherCode(currentCityCode);
        queryXMLWeatherCode(currentCityCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.weather_info);
        setContentView(R.layout.weather_info);
        // 为更新按钮添加点击事件
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        // 定位代码
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        List<String> permissionList = new ArrayList<>();
        requestLocation();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()) {
            Toast.makeText(MainActivity.this, "定位出问题了！", Toast.LENGTH_LONG).show();
            String []permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }else {
            requestLocation();
        }

        // 网络检测
        if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            Log.d("weather", "ok");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        }else {
            Log.d("weather", "no");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }

        // 为选择城市的图片添加点击事件
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        // 初始化控件
        initView();

    }

    private void requestLocation() {
        Log.d("requestLocation", "start");
        initLocation();
        mLocationClient.start();
    }
    // 初始化定位
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //option.setScanSpan(5000);

        //option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        // 把经纬度转换为看得懂的地址
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        //option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        Log.d("addType", option.getAddrType().toString());
    }

    protected void onDestory() {
        super.onDestroy();
        mLocationClient.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0) {
                    for(int result : grantResults) {
                        if(result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有的权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return ;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度: ").append(location.getLatitude()).append("\n");
            currentPosition.append("经度: ").append(location.getLongitude()).append("\n");
            currentPosition.append("国家: ").append(location.getCountry()).append("\n");
            currentPosition.append("省: ").append(location.getProvince()).append("\n");
            currentPosition.append("市: ").append(location.getCity()).append("\n");
            currentPosition.append("城市编号: ").append(location.getCityCode()).append("\n");
            currentPosition.append("区: ").append(location.getDirection()).append("\n");
            currentPosition.append("街道: ").append(location.getStreet()).append("\n");
            currentPosition.append("定位方式: ");
            if(location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            }else if(location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            }
            Toast.makeText(MainActivity.this, currentPosition.toString(), Toast.LENGTH_LONG).show();
            Log.d("position", currentPosition.toString());
        }
    }

    /**
     * 更新按钮的点击事件
     * */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
            Log.d("currentCityName", city_name_Tv.getText().toString());
            i.putExtra("currentCityName", city_name_Tv.getText());
            i.putExtra("currentCityCode", currentCityCode);
            // 切换到选择城市界面
            //startActivity(i);
            startActivityForResult(i, 1);
        }

        if(view.getId() == R.id.title_update_btn) {
            // SharedPreferences用于数据的存储与读取
            ///SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            //String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            // 太原城市代码
            //String cityCode = sharedPreferences.getString("main_city_code", "101160101");
            //Log.d("myWeather", cityCode);
            // 根据城市的编号查询天气情况
            // 两个数据接口取长补短
            queryWeatherCode(currentCityCode);
            queryXMLWeatherCode(currentCityCode);
        }
    }
    // 解析返回的数据
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // 发送请求
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public void queryWeatherCode(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=CN" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d("responseText", responseText);
                final Weather weather = handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            //mWeatherId = weather.basic.weatherId;
                            //showWeatherInfo(weather);
                            Log.d("cityName", weather.basic.cityName);
                            Log.d("weatherId", weather.basic.weatherId);
                            Log.d("update", weather.basic.update.toString());
                            Log.d("aqi", weather.aqi.city.aqi);
                            Log.d("pm25", weather.aqi.city.pm25);
                            Log.d("temp", weather.now.temperature);
                            Log.d("more", weather.now.more.info);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = weather;
                            handler.sendMessage(msg);
                        } else {
                            Toast.makeText(MainActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        //swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        //swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }
    /**
     * @param cityCode
     * */
    private void queryXMLWeatherCode(final String cityCode) {
        // 请求到的是xml格式的数据
        //final String address = "http://guolin.tech/api/weather";
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("weather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("weather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("weather", responseStr);
                    // 解析XML数据
                    todayWeather = parseXML(responseStr);
                    //updateXMLTodayWeather(todayWeather);
                    Message msg = new Message();
                    msg.obj = todayWeather;
                    msg.what = 2;
                    handler.sendMessage(msg);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int hightCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("weather", "parseXML");
            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        // resp是xml的第一个标签
                        if(xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather != null) {
                            if(xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                                Log.d("weather", "city: " + xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                                Log.d("weather", "updatetime: " + xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                                Log.d("weather", "shidu: " + xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                                Log.d("weather", "wendu: " + xmlPullParser.getText());
                                // 传来的数据不包含pm2.5
                            }else if(xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                                Log.d("weather", "pm25: " + xmlPullParser.getText());
                                // 传来的数据不包含quality
                            }else if(xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                                Log.d("weather", "quality: " + xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                Log.d("weather", "fengxiang: " + xmlPullParser.getText());
                                fengxiangCount ++;
                            }else if(xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                Log.d("weather", "fengli: " + xmlPullParser.getText());
                                fengliCount ++;
                            }else if(xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                Log.d("weather", "dateCount: " + xmlPullParser.getText());
                                dateCount ++;
                            }else if(xmlPullParser.getName().equals("high") && hightCount == 0) {
                                eventType = xmlPullParser.next();
                                // 截取温度的数据
                                todayWeather.setHigh(xmlPullParser.getText().substring(3));
                                Log.d("weather", "high: " + xmlPullParser.getText());
                                hightCount ++;
                            }else if(xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                // 截取温度的数据
                                todayWeather.setLow(xmlPullParser.getText().substring(3));
                                Log.d("weather", "low: " + xmlPullParser.getText());
                                lowCount ++;
                            }else if(xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                Log.d("weather", "type: " + xmlPullParser.getText());
                                typeCount ++;
                            }
                        }
                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    void updateTodayWeather(Weather todayWeather) {
        city_name_Tv.setText(todayWeather.basic.cityName + "天气");
        cityTv.setText(todayWeather.basic.cityName);
        timeTv.setText(todayWeather.basic.update.updateTime + "发布");
        current_temperature.setText("当前温度 " + todayWeather.now.temperature);
        //humidityTv.setText("湿度：" + todayWeather.);
        pmDataTv.setText(todayWeather.aqi.city.pm25);
        pmQualityTv.setText(todayWeather.aqi.city.aqi);
//        weekTv.setText(todayWeather.getDate());
//        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.now.more.info);
//        windTv.setText("风力:" + todayWeather.getFengli());
        // 更新指示天气的图像
        updateWeatherImg(todayWeather.now.more.info);

        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }

    void updateXMLTodayWeather(TodayWeather todayWeather) {

        humidityTv.setText("湿度：" + todayWeather.getShidu());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        windTv.setText("风力:" + todayWeather.getFengli());
    }
    void updateWeatherImg(String type) {
        switch(type) {
            case WEATHER_BAOXUE:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case WEATHER_BAOYU:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case WEATHER_DABAOYU:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case WEATHER_DAXUE:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case WEATHER_DAYU:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case WEATHER_DUOYUN:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case WEATHER_LEIZHENYU:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case WEATHER_LEIZHENYUBINGBAO:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case WEATHER_QING:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case WEATHER_SHACHENBAO:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case WEATHER_TEDABAOYU:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case WEATHER_WU:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case WEATHER_XIAOXUE:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case WEATHER_XIAOYU:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case WEATHER_YIN:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case WEATHER_YUJIAXUE:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case WEATHER_ZHENXUE:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case WEATHER_ZHENYU:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case WEATHER_ZHONGXUE:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case WEATHER_ZHONGYU:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            default:
                break;
        }
    }
    // 接收选择地址销毁之后，传来的消息
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            // 记录下newCityCode， 刷新的时候要用到
            currentCityCode = newCityCode;
            Log.d("weather", "选择的城市代码为" + newCityCode);
            // 如果网络可用, 就请求新的天气数据
            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("weather", "网络ok");
                queryWeatherCode(newCityCode);
            }else {
                Log.d("weather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了! ", Toast.LENGTH_LONG).show();
            }
        }
    }
}
