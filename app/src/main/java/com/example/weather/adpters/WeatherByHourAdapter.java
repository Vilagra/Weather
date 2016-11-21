package com.example.weather.adpters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weather.R;
import com.example.weather.entity.WeatherByHours;

import java.util.List;

/**
 * Created by Vilagra on 16.11.2016.
 */

public class WeatherByHourAdapter extends RecyclerView.Adapter<WeatherByHourAdapter.ViewHolder> {
    List<WeatherByHours> list;

    public WeatherByHourAdapter(List<WeatherByHours> list) {
        this.list = list;
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder{
        private  TextView date;
        private TextView dateHour;
        private TextView  temperature;
        private TextView  wind;
        private ImageView icon;


        public ViewHolder(View itemView, TextView date, TextView dateHour, ImageView icon, TextView temperature, TextView wind) {
            super(itemView);
            this.date=date;
            this.dateHour = dateHour;
            this.icon = icon;
            this.temperature = temperature;
            this.wind = wind;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public WeatherByHourAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather_by_hour,parent,false);
        TextView date = (TextView) cardView.findViewById(R.id.date2);
        TextView dateHour = (TextView) cardView.findViewById(R.id.hour);
        TextView  wind = (TextView) cardView.findViewById(R.id.wind_by_hour);
        TextView  temp = (TextView) cardView.findViewById(R.id.temperetureByHour);
        ImageView icon = (ImageView) cardView.findViewById(R.id.imageWeather2);
        return new WeatherByHourAdapter.ViewHolder(cardView,date,dateHour,icon,temp,wind);
    }

    @Override
    public void onBindViewHolder(WeatherByHourAdapter.ViewHolder holder, int position) {
        holder.date.setText(list.get(position).getStringDate());
        holder.dateHour.setText(list.get(position).getStringHour());
        holder.wind.setText(list.get(position).getWindString());
        holder.temperature.setText(list.get(position).getTemperatureString());
        holder.icon.setImageResource(list.get(position).getIdDrawable());

    }
}

