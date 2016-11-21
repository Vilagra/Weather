package com.example.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;

public class Settings extends Activity {
    SharedPreferences sharedPreferences;
    boolean unitsIsChanged;
    SharedPreferences.Editor editor;
    Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getValueFromPreferences();
        apply=(Button) findViewById(R.id.apply);

    }
    //sets slidebars depending on the settings
    private void getValueFromPreferences(){
        sharedPreferences = getSharedPreferences(getString(R.string.preference),Context.MODE_PRIVATE);
        String temper = sharedPreferences.getString(MainActivity.UNIT_TEMPRATURE,null);
        String wind = sharedPreferences.getString(MainActivity.UNIT_WIND,null);
        String time = sharedPreferences.getString(MainActivity.UNIT_TIME,null);
        String date = sharedPreferences.getString(MainActivity.UNIT_DATE,null);
        String wifi = sharedPreferences.getString(MainActivity.WIFI,null);

        if(temper.equals(getString(R.string.celcius))){
            ((RadioButton)findViewById(R.id.celcius)).setChecked(true);
        }else {
            ((RadioButton)findViewById(R.id.farengeit)).setChecked(true);
        }

        if(wind.equals(getString(R.string.mc))){
            ((RadioButton)findViewById(R.id.m_c)).setChecked(true);
        }else if(wind.equals(getString(R.string.km_h))){
            ((RadioButton)findViewById(R.id.km_h)).setChecked(true);
        }else{
            ((RadioButton)findViewById(R.id.m_h)).setChecked(true);
        }

        if(time.equals(getString(R.string.h24))){
            ((RadioButton)findViewById(R.id.h24)).setChecked(true);
        }else {
            ((RadioButton)findViewById(R.id.h12)).setChecked(true);
        }

        if(date.equals(getString(R.string.dm))){
            ((RadioButton)findViewById(R.id.dm)).setChecked(true);
        }else {
            ((RadioButton)findViewById(R.id.md)).setChecked(true);
        }

        if(wifi.equals(getString(R.string.on))){
            ((Switch)findViewById(R.id.wifi)).setChecked(true);
        }else {
            ((Switch)findViewById(R.id.wifi)).setChecked(false);
        }
    }

    //if button "apply" was not pushed, discards changes
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(editor!=null){
            editor.clear();
        }
    }
    //sets changes preferences
    public void changePreferences(View view){
        if (editor==null) {
            editor = sharedPreferences.edit();
        }
        switch (view.getId()){
            case R.id.celcius:
                editor.putString(MainActivity.UNIT_TEMPRATURE,getString(R.string.celcius));
                break;
            case R.id.farengeit:
                editor.putString(MainActivity.UNIT_TEMPRATURE,getString(R.string.farengeit));
                break;
            case R.id.m_c:
                editor.putString(MainActivity.UNIT_WIND,getString(R.string.mc));
                break;
            case R.id.km_h:
                editor.putString(MainActivity.UNIT_WIND,getString(R.string.km_h));
                break;
            case R.id.m_h:
                editor.putString(MainActivity.UNIT_WIND,getString(R.string.m_h));
                break;
            case R.id.h12:
                editor.putString(MainActivity.UNIT_TIME,getString(R.string.h12));
                break;
            case R.id.h24:
                editor.putString(MainActivity.UNIT_TIME,getString(R.string.h24));
                break;
            case R.id.dm:
                editor.putString(MainActivity.UNIT_DATE,getString(R.string.dm));
                break;
            case R.id.md:
                editor.putString(MainActivity.UNIT_DATE,getString(R.string.md));
                break;
        }
        unitsIsChanged=true;
        apply.setEnabled(true);
    }

    public void onWiFiClicked(View view){
        boolean on = ((Switch) view).isChecked();
        if (editor==null) {
            editor = sharedPreferences.edit();
        }
        if(on){
            editor.putString(MainActivity.WIFI,"ON");
        }else {
            editor.putString(MainActivity.WIFI,"OFF");
        }
        apply.setEnabled(true);
    }


    public void onClickOk(View view){
        this.finish();
    }

    //fixes changes
    public void apply(View view){
        editor.commit();
        if(unitsIsChanged) {
            MainActivity.refreshDisplay = true;
        }
        apply.setEnabled(false);

    }
}
