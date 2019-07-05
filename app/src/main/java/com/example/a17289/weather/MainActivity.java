package com.example.a17289.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.a17289.bean.TodayWeather;
import com.example.a17289.util.NetUtil;

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

// dp * ppi / 160 = px
// mdpi -> 160
// hdpi -> 240
// xxdpi -> 320
// xxdpi -> 480

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText textUser;
    private EditText textPass;
    private static final int UPDATE_TODAY_WEATHER = 1;
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
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    // 通过消息机制来更新UI界面的数据
    // 主线程将接收消息
    private Handler mHander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch(msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        // 加载weather_info布局
   //     setContentView(R.layout.land);
        // 选出控件 按钮 文本框
//        btn = (Button) findViewById(R.id.land);

//        textUser = (EditText) findViewById(R.id.et_user);
//        textPass = (EditText) findViewById(R.id.et_password);

        // 刚开始设置按钮不可用
        //btn.setEnabled(false);
        // 不可用时设置按钮为灰色
        //btn.setBackgroundColor(Color.parseColor("#B0C4DE"));
        // 字体颜色也设置为灰色
        //btn.setTextColor(Color.parseColor("#708090"));
        // 为按钮设置点击事件
        //btn.setOnClickListener(this);
        // 为更新按钮添加点击事件
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

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
        // 给按钮添加change事件
//        textUser.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.d("s: ", s.toString());
//                Log.d("start: ", s.toString());
//                Log.d("count: ", s.toString());
//                Log.d("after: ", s.toString());
//                return ;
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                return ;
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                btn.setEnabled(s.length() >= 6 && textUser.getText().toString().length() >= 4);
//                return ;
//            }
//        });

//        textPass.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                return ;
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                btn.setEnabled(s.length() >= 6 && textUser.getText().toString().length() >= 4);
//                Log.d("Editable", s.toString());
//                return ;
//            }
//        });
    }
    /**
     * 更新按钮的点击事件
     * */
    @Override
    public void onClick(View view) {
//          if(view.getId() == R.id.land) {
//              EditText textUser = (EditText) findViewById(R.id.et_user);
//              EditText textPass = (EditText) findViewById(R.id.et_password);
//              Log.d("user", textUser.getText().toString());
//              Log.d("password", textPass.getText().toString());
//              // admin | admin888
//              if(textUser.getText().toString().equals("admin") && textPass.getText().toString().equals("admin888")) {
//                  // 跳转到Second活动
//                  Intent intent = new Intent(MainActivity.this, Second.class);
//                  Log.d("type", intent.toString());
//                  startActivityForResult(intent, 1);
//                  Log.d("type", intent.toString());
//                  Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
//              }else {
//                  Toast.makeText(MainActivity.this, "登陆失败！", Toast.LENGTH_LONG).show();
//              }
//              Intent intent = new Intent(MainActivity.this, Second.class);
//              startActivityForResult(intent, 1);
//          }
        if(view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
            // 切换到选择城市界面
            //startActivity(i);
            startActivityForResult(i, 1);
        }

        if(view.getId() == R.id.title_update_btn) {
            // SharedPreferences用于数据的存储与读取
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            // 太原城市代码
            //String cityCode = sharedPreferences.getString("main_city_code", "101160101");
            Log.d("myWeather", cityCode);
            // 根据城市的编号查询天气情况
            queryWeatherCode(cityCode);
        }
    }

    /**
     * @param cityCode
     * */
    private void queryWeatherCode(String cityCode) {
        // 请求到的是xml格式的数据
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("weather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
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
                    if(todayWeather != null){
                        Log.d("weather", todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        // msg包含获取到的各种数据
                        mHander.sendMessage(msg);
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }finally {
                    if(con != null) {
                        con.disconnect();
                    }
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

    void updateTodayWeather(TodayWeather todayWeather) {
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        // pm25数据缺失
        pmDataTv.setText(todayWeather.getPm25());
        // quality数据缺失
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        // 更新指示天气的图像
        updateWeatherImg(todayWeather.getType());

        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
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
//    @Override
//    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case 1:
//                if(resultCode == RESULT_OK) {
//                    String returnDate = data.getStringExtra("data_result");
//                    Toast.makeText(MainActivity.this, returnDate, Toast.LENGTH_LONG).show();
//                }
//                break;
//            default:
//        }
//    }
}
