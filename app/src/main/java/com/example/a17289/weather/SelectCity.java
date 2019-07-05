package com.example.a17289.weather;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.ImageWriter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.a17289.app.MyApplication;
import com.example.a17289.bean.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 17289 on 2019/6/29.
 */

public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private EditText mEditText;
    private ListView mList;
    private  List<City> cityList;
    private MyAdapter myadapter;
    private List<City> filterDateList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        // 初始化列表
        initViews();
    }

    private void initViews() {
        //为mBackBtn设置监听事件
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        // 搜索框
        mEditText = (EditText) findViewById(R.id.search_city);
        mList = (ListView) findViewById(R.id.city_list);
        // myApplication 里包含City类型的数据列
        MyApplication myApplication = (MyApplication) getApplication();

        // 获得ArrayList<City>类型的数据列表
        cityList = myApplication.getCityList();
        // 把数据放入适配器中
        for(City city : cityList) {
            filterDateList.add(city);
        }
        myadapter = new MyAdapter(SelectCity.this, R.layout.city_item, filterDateList);
        mList.setAdapter(myadapter);
        //为cityList添加点击事件
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                City city = filterDateList.get(position);
                Intent i = new Intent();
                i.putExtra("cityCode", city.getNumber());
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", "101160101");
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }
}
