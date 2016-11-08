package com.example.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
