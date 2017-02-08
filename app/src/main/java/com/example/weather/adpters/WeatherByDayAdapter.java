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

import butterknife.BindView;
import butterknife.ButterKnife;

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
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.dayOfWeek)
        TextView  day;
        @BindView(R.id.maxT)
        TextView  max;
        @BindView(R.id.minT)
        TextView  min;
        @BindView(R.id.imageWeather)
        ImageView icon;


        public ViewHolder(CardView itemView) {
            super(itemView);
            cardView=itemView;
            ButterKnife.bind(this,cardView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather_by_day,parent,false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        WeatherByDay weatherByDay= list.get(position);
        holder.date.setText(weatherByDay.getStringDate(sharedPreferences.getString(ctx.getString(R.string.dateFormat),null)));
        holder.day.setText(weatherByDay.getDayOfWeekShort());
        holder.max.setText(weatherByDay.getMaxTString(sharedPreferences.getString(ctx.getString(R.string.temperature),null)));
        holder.min.setText(weatherByDay.getMinTString(sharedPreferences.getString(ctx.getString(R.string.temperature),null)));
        holder.icon.setImageResource(weatherByDay.getIdDrawable(ctx));
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
