package com.example.weather;

import android.location.Geocoder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Vilagra on 08.11.2016.
 */

public class Example {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://api.darksky.net/forecast/fff553af3244a00bd36d3c0b398dce88/49.5850,36.1509?units=auto&lang=ru");
            URLConnection conn = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String string;
            while ((string=bufferedReader.readLine())!=null){
                System.out.println(string);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(1478901600L*1000);
            Date date = new Date();
            Date date2=calendar.getTime();
            System.out.println(date.getTime());
            System.out.println(date2.getTime()+" "+date2);
            System.out.println(new SimpleDateFormat("dd.MM EE").format(date2.getTime()));
            System.out.println();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
