package com.example.weather.adpters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.entity.WeatherByHours;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vilagra on 16.11.2016.
 */

public class WeatherByHourAdapter extends RecyclerView.Adapter<WeatherByHourAdapter.ViewHolder> {
    List<WeatherByHours> list;
    SharedPreferences sharedPreferences;
    Context ctx;

    public WeatherByHourAdapter(Context ctx,List<WeatherByHours> list, SharedPreferences sharedPreferences) {
        this.ctx=ctx;
        this.list = list;
        this.sharedPreferences=sharedPreferences;
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date2)
        TextView date;
        @BindView(R.id.hour)
        TextView dateHour;
        @BindView(R.id.temperetureByHour)
        TextView temperature;
        @BindView(R.id.wind_by_hour)
        TextView wind;
        @BindView(R.id.imageWeather2)
        ImageView icon;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public WeatherByHourAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather_by_hour,parent,false);
        return new WeatherByHourAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(WeatherByHourAdapter.ViewHolder holder, int position) {
        WeatherByHours weatherByHours = list.get(position);
        holder.date.setText(weatherByHours.getStringDate(sharedPreferences.getString(ctx.getString(R.string.dateFormat),null)));
        holder.dateHour.setText(weatherByHours.getStringHour(sharedPreferences.getString(ctx.getString(R.string.time_format),null)));
        holder.wind.setText(weatherByHours.getWindString(sharedPreferences.getString(ctx.getString(R.string.wind_speed),null)));
        holder.temperature.setText(weatherByHours.getTemperatureString(sharedPreferences.getString(ctx.getString(R.string.temperature),null)));
        holder.icon.setImageResource(weatherByHours.getIdDrawable(ctx));

    }
}

