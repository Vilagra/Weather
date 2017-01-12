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
import com.example.weather.entity.WeatherByDay;

import java.util.List;

/**
 * Created by Vilagra on 11.11.2016.
 */

public class WeatherByDayAdapter extends RecyclerView.Adapter<WeatherByDayAdapter.ViewHolder>{

    List<WeatherByDay> list;
    SharedPreferences sharedPreferences;
    Context ctx;
    private Listener listener;

    public interface Listener{
        void onClick(int position);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public WeatherByDayAdapter(Context ctx,List<WeatherByDay> list,SharedPreferences sharedPreferences) {
        this.ctx=ctx;
        this.list = list;
        this.sharedPreferences=sharedPreferences;
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView date;
        TextView  day;
        TextView  max;
        TextView  min;
        ImageView icon;


        public ViewHolder(CardView itemView, TextView date, TextView day, ImageView icon, TextView max, TextView min) {
            super(itemView);
            cardView=itemView;
            this.date = date;
            this.day = day;
            this.icon = icon;
            this.max = max;
            this.min = min;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather_by_day,parent,false);
        TextView date = (TextView) cardView.findViewById(R.id.date);
        TextView  day = (TextView) cardView.findViewById(R.id.dayOfWeek);
        TextView  max = (TextView) cardView.findViewById(R.id.maxT);
        TextView  min = (TextView) cardView.findViewById(R.id.minT);
        ImageView icon = (ImageView) cardView.findViewById(R.id.imageWeather);
        return new ViewHolder(cardView,date,day,icon,max,min);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        WeatherByDay weatherByDay= list.get(position);
        holder.date.setText(weatherByDay.getStringDate(sharedPreferences.getString(ctx.getString(R.string.dateFormat),null)));
        holder.day.setText(weatherByDay.getDayOfWeekShort());
        holder.max.setText(weatherByDay.getMaxTString(sharedPreferences.getString(ctx.getString(R.string.temperature),null)));
        holder.min.setText(weatherByDay.getMinTString(sharedPreferences.getString(ctx.getString(R.string.temperature),null)));
        holder.icon.setImageResource(weatherByDay.getIdDrawable());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onClick(position);
                }
            }
        });

    }
}
