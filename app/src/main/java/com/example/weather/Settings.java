package com.example.weather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;

public class Settings extends Activity {
    SharedPreferences sharedPreferences;
    boolean valueOfSlidersIsChanged;
    Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSliderByPreferences();
        apply=(Button) findViewById(R.id.apply);

    }

    //sets changes preferences
    public void changePreferences(View view){
        valueOfSlidersIsChanged =true;
        apply.setEnabled(true);
    }


    public void onClickOk(View view){
        this.finish();
    }

    //fixes changes
    public void apply(View view){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainActivity.UNIT_TEMPRATURE,getCurrentValueOfTemperature());
        editor.putString(MainActivity.UNIT_WIND,getCurrentValueOfWind());
        editor.putString(MainActivity.UNIT_DATE,getCurrentValueOfDate());
        editor.putString(MainActivity.UNIT_TIME,getCurrentValueOfTime());
        editor.putString(MainActivity.WIFI,getCurrentValueOfWifi());
        editor.commit();
        setResult(RESULT_OK);
        apply.setEnabled(false);
    }
    private void setSliderByPreferences(){
        sharedPreferences = getSharedPreferences(getString(R.string.preference),Context.MODE_PRIVATE);
        setSliderTemperatureByPreferences();
        setSliderWindByPreferences();
        setSliderTimeByPreferences();
        setSliderDateByPreferences();
        setSliderWiFiByPreferences();
    }
    private void setSliderTemperatureByPreferences(){
        String temper = sharedPreferences.getString(MainActivity.UNIT_TEMPRATURE,null);
        if(temper.equals(getString(R.string.celcius))){
            ((RadioButton)findViewById(R.id.celcius)).setChecked(true);
        }else {
            ((RadioButton)findViewById(R.id.farengeit)).setChecked(true);
        }
    }
    private void setSliderWindByPreferences(){
        String wind = sharedPreferences.getString(MainActivity.UNIT_WIND,null);
        if(wind.equals(getString(R.string.mc))){
            ((RadioButton)findViewById(R.id.m_c)).setChecked(true);
        }else if(wind.equals(getString(R.string.km_h))){
            ((RadioButton)findViewById(R.id.km_h)).setChecked(true);
        }else{
            ((RadioButton)findViewById(R.id.m_h)).setChecked(true);
        }

    }
    private void setSliderTimeByPreferences(){
        String time = sharedPreferences.getString(MainActivity.UNIT_TIME,null);
        if(time.equals(getString(R.string.h24))){
            ((RadioButton)findViewById(R.id.h24)).setChecked(true);
        }else {
            ((RadioButton)findViewById(R.id.h12)).setChecked(true);
        }

    }
    private void setSliderDateByPreferences(){
        String date = sharedPreferences.getString(MainActivity.UNIT_DATE,null);
        if(date.equals(getString(R.string.dm))){
            ((RadioButton)findViewById(R.id.dm)).setChecked(true);
        }else {
            ((RadioButton)findViewById(R.id.md)).setChecked(true);
        }

    }
    private void setSliderWiFiByPreferences(){
        String wifi = sharedPreferences.getString(MainActivity.WIFI,null);
        if(wifi.equals(getString(R.string.on))){
            ((Switch)findViewById(R.id.wifi)).setChecked(true);
        }else {
            ((Switch)findViewById(R.id.wifi)).setChecked(false);
        }
    }

    private String getCurrentValueOfTemperature(){
        if(((RadioButton)findViewById(R.id.celcius)).isChecked()){
            return getString(R.string.celcius);
        }else{
            return getString(R.string.farengeit);
        }
    }
    private String getCurrentValueOfWind(){
        if(((RadioButton)findViewById(R.id.m_c)).isChecked()){
            return getString(R.string.mc);
        }else if (((RadioButton)findViewById(R.id.km_h)).isChecked()){
            return getString(R.string.km_h);
        }
        else{
            return getString(R.string.m_h);
        }
    }
    private String getCurrentValueOfTime(){
        if(((RadioButton)findViewById(R.id.h12)).isChecked()){
            return getString(R.string.h12);
        }else{
            return getString(R.string.h24);
        }
    }
    private String getCurrentValueOfDate(){
        if(((RadioButton)findViewById(R.id.dm)).isChecked()){
            return getString(R.string.dm);
        }else{
            return getString(R.string.md);
        }
    }
    private String getCurrentValueOfWifi(){
        boolean on = ((Switch) findViewById(R.id.wifi)).isChecked();
        if(on){
            return getString(R.string.on);
        }else {
            return getString(R.string.off);
        }
    }


}
