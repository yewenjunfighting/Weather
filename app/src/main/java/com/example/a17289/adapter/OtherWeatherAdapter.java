package com.example.a17289.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a17289.bean.OtherDayWeather;
import com.example.a17289.weather.R;

import java.util.List;

/**
 * Created by 17289 on 2019/7/8.
 */

public class OtherWeatherAdapter extends RecyclerView.Adapter<OtherWeatherAdapter.ViewHolder>{
    private List<OtherDayWeather>other_day_weather;
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView weather_image;
        TextView weather_type,
                 weather_fengli,
                 weather_wendu,
                 weather_day;
        public ViewHolder(View view) {
            super(view);
            weather_image = (ImageView) view.findViewById(R.id.weather_img);
            weather_day = (TextView) view.findViewById(R.id.week_today);
            weather_type = (TextView) view.findViewById(R.id.climate);
            weather_fengli = (TextView) view.findViewById(R.id.wind);
            weather_wendu = (TextView) view.findViewById(R.id.temperature);
        }
    }
    public OtherWeatherAdapter(List<OtherDayWeather> otherDay) {
        other_day_weather = otherDay;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.otherday_weather, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OtherDayWeather otherDay = other_day_weather.get(position);
        holder.weather_day.setText(otherDay.getDate());
        holder.weather_image.setImageResource(otherDay.getImageId());
        holder.weather_wendu.setText(otherDay.getLow() + "℃~" + otherDay.getHigh() + "℃");
        holder.weather_type.setText(otherDay.getType());
        holder.weather_fengli.setText(otherDay.getFengli());
    }
    @Override
    public int getItemCount() {
        return other_day_weather.size();
    }
}
