package com.example.weather;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.buttonRefresh);

        button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                refreshData();
            }
        });

    }

    // фукция загрузки температуры


    // функция обновления показаний температуры
    public void refreshData()
    {
        EditText latitude=(EditText) findViewById(R.id.shirota);
        EditText longitude=(EditText) findViewById(R.id.dolgota);
        StringBuilder prepareUrl = new StringBuilder("https://api.darksky.net/forecast/fff553af3244a00bd36d3c0b398dce88/");
        prepareUrl.append(latitude.getText().toString());
        prepareUrl.append(",");
        prepareUrl.append(longitude.getText().toString());
        prepareUrl.append("?units=auto&lang=ru");
        new MyAsyncTask().execute(prepareUrl.toString());

        // tTemper.setText(bashtemp.concat("°")); // отображение температуры
    }

    class MyAsyncTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            return getData(params[0]);
        }

        public String getData(String urlsite) {
            StringBuilder res=new StringBuilder("");
            try {
                URL url = new URL(urlsite);
                URLConnection conn = url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str;
                while ((str=bufferedReader.readLine())!=null){
                    res.append(str);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            TextView city = (TextView) findViewById(R.id.city);
            TextView temperature = (TextView) findViewById(R.id.textviewTemperature);
            TextView description = (TextView) findViewById(R.id.description);
            TextView humidity = (TextView) findViewById(R.id.humidity);
            TextView wind = (TextView) findViewById(R.id.wind);
            try {
                JSONObject jsonObject = new JSONObject(s);
                city.setText(jsonObject.getString("timezone"));
                JSONObject currently = jsonObject.getJSONObject("currently");
                temperature.setText(currently.getDouble("temperature")+"°");
                description.setText(currently.getString("summary"));
                humidity.setText(getString(R.string.humidity)+" "+currently.getDouble("humidity")+"");
                wind.setText(getString(R.string.wind_speed)+" "+currently.getDouble("windSpeed"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
