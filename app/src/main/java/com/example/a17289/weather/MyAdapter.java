package com.example.a17289.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.a17289.bean.City;

import java.util.List;

/**
 * Created by 17289 on 2019/7/5.
 */

public class MyAdapter extends ArrayAdapter<City>{
    private int resourceId;
    public MyAdapter(Context context, int textViewResourceId, List<City> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        City city = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView cityName = (TextView) view.findViewById(R.id.city_name);
        cityName.setText(city.getCity());
        return view;
    }
}
