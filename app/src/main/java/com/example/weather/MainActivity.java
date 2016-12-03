package com.example.weather;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.adpters.WeatherByDayAdapter;
import com.example.weather.adpters.WeatherByHourAdapter;
import com.example.weather.entity.CurrentDisplayedWeather;
import com.example.weather.entity.Weather;
import com.example.weather.entity.WeatherByDay;
import com.example.weather.entity.WeatherByHours;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    private ShareActionProvider shareActionProvider;
    private SharedPreferences sharedPreferences;
    private DataManager dataManager;

    public static boolean refreshDisplay;

    public static final String UNIT_TEMPRATURE = "unitTemperature";
    public static final String UNIT_WIND = "unitWind";
    public static final String UNIT_TIME = "unitTime";
    public static final String UNIT_DATE = "unitDate";
    public static final String WIFI = "wifi";


    public final String CITY = "city";
    public final String RADIOBUTTON_ID = "radoButtonId";
    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 75443;

    EditText enterCity;
    TextView cityV;
    TextView temperatureV;
    TextView descriptionV;
    TextView locationV;
    TextView dayOfWeekV;
    ImageView pictureV;
    TextView humidityV;
    TextView windV;
    TextView probabilityV;
    RecyclerView recycler;
    RadioGroup radioGroupV;
    TextView linkV;
    MenuItem menuShareItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enterCity = (EditText) findViewById(R.id.enterCity);
        cityV = (TextView) findViewById(R.id.city);
        temperatureV = (TextView) findViewById(R.id.textviewTemperature);
        descriptionV = (TextView) findViewById(R.id.description);
        humidityV = (TextView) findViewById(R.id.humidity);
        windV = (TextView) findViewById(R.id.wind);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        locationV = (TextView) findViewById(R.id.city_full_name);
        pictureV = (ImageView) findViewById(R.id.picture);
        dayOfWeekV = (TextView) findViewById(R.id.full_day_of_week);
        probabilityV = (TextView) findViewById(R.id.probability);
        radioGroupV = (RadioGroup) findViewById(R.id.radio_group);
        linkV = (TextView) findViewById(R.id.link);
        dataManager= new DataManager(getSharedPreferences(getString(R.string.myData), Context.MODE_PRIVATE));
        setPreferences();  //by first start of application sets default preferences; !!!Не знаю где оставлять этот коммент по этому методу тут или над самим методом или там и там? Сделаю в обоих
        if (dataManager.getCurrentDisplay() != null) { //by screen rotation recovers state by weather object !!!Вначале я сохранял состояние всех вьюшек в бандстейт, но их очень много и я
                                                        //решил создавать объект, восстанавливать состояние по нему и хранить его в myapplication
            updateView(dataManager.getNameOfCity(), dataManager.getCurrentDisplay(), dataManager.getLocation());
            if (savedInstanceState != null) {
                RadioButton radioButton = (RadioButton) findViewById(savedInstanceState.getInt(RADIOBUTTON_ID));
                radioButton.setChecked(true);
            }
            updateRecycler();
        }/* else if (dataManager.getNameOfCity() != null) { //by start of application restores last entered city of the previous start
            enterCity.setText(sharedPreferences.getString(CITY, null));
            refreshData();
        }*/
        final Button button = (Button) findViewById(R.id.buttonRefresh);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //these rows are present in order to hide keyboard
                imm.hideSoftInputFromWindow(enterCity.getWindowToken(), 0);                                  // when button "найти" is pressed
                refreshData();
            }
        });


    }

    public void onStart() {
        super.onStart();
        if (refreshDisplay) {  //if settings of units were changed, updates display with new units, for example fahrengheit or celcius
            updateView(dataManager.getNameOfCity(), dataManager.getCurrentDisplay(), dataManager.getLocation());
            updateRecycler();
        }
        refreshDisplay = false;
    }

    //by first start of application sets default preferences;
    private void setPreferences() {
        sharedPreferences = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isCustomized", false) == false) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(UNIT_TEMPRATURE, getString(R.string.celcius));
            editor.putString(UNIT_WIND, getString(R.string.mc));
            editor.putString(UNIT_TIME, getString(R.string.h24));
            editor.putString(UNIT_DATE, getString(R.string.dm));
            editor.putString(WIFI, getString(R.string.on));
            editor.putBoolean("isCustomized", true);
            editor.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RADIOBUTTON_ID, radioGroupV.getCheckedRadioButtonId());
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putString(CITY, myApplication.getNameOfCity()); //before application closes, saves last city  !!!я не уверен что делаю это в правильном месте
        //editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuShareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) menuShareItem.getActionProvider();
        checkPermission();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //sets default intent for shareActionProvider  !!!вот тут я вобще не уверен, я делал приблизительно как в книге, я устанавливаю провайдер в который устанавливаю интент с битмапом скрина
    // экрана,которому при каждом обновлении экрана устанавливаю битмап, после этого стало заметно притормаживать, думаю лучше делать это только при необходимости и без этого провайдера, но оставил так
    // потому что в книге так, может, ты мне расскажешь, зачем так делается
    private void setIntent() {
        Bitmap bitmap = getBitmapFromView((LinearLayout) findViewById(R.id.main));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, getImageUri(this, bitmap));
        shareActionProvider.setShareIntent(intent);
    }

    //next 2 methods I googled in order to get screenshot
    private Bitmap getBitmapFromView(LinearLayout view) {
        try {
            view.setDrawingCacheEnabled(true);
            view.measure(View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache(true);
            Bitmap returnedBitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            return returnedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                    inImage, "", "");
            return Uri.parse(path);
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public void refreshData() {
        dataManager.setNameOfCity(enterCity.getText().toString());
        if (checkConnection()) {
            GetLatLongAsyncTask latlng = new GetLatLongAsyncTask(
                    dataManager.getNameOfCity().replaceAll(" ", "%20"));
            latlng.execute();
        }
    }

    private void updateView(String city, CurrentDisplayedWeather currentDisplayedWeather, String location) {
        city = city.substring(0, 1).toUpperCase() + city.substring(1);
        cityV.setText(city);
        temperatureV.setText(currentDisplayedWeather.getTemperatureString(sharedPreferences.getString(UNIT_TEMPRATURE,null)));
        locationV.setText(location);
        descriptionV.setText(currentDisplayedWeather.getSummary());
        humidityV.setText(getString(R.string.humidity) + " " + currentDisplayedWeather.getHumidityString());
        windV.setText(getString(R.string.wind_speed) + " " + currentDisplayedWeather.getWindString(sharedPreferences.getString(UNIT_WIND,null)));
        pictureV.setImageResource(currentDisplayedWeather.getIdDrawable());
        probabilityV.setText(getString(R.string.probability) + " " + currentDisplayedWeather.getPrecipProbString());
        dayOfWeekV.setText(currentDisplayedWeather.getDayOfWeekLong());
        linkV.setText(Html.fromHtml("<a href=\"https://icons8.com/web-app/3350/Clouds\">Clouds icon credits</a>"));
        linkV.setMovementMethod(LinkMovementMethod.getInstance());
        if(shareActionProvider!=null) {
            checkPermission();
        }
    }


    private void updateRecycler() {
        int id = radioGroupV.getCheckedRadioButtonId();
        RecyclerView.Adapter adapter = null;
        switch (id) {
            case R.id.byDay:
                adapter = new WeatherByDayAdapter(dataManager.getWeatherByDays(),sharedPreferences);
                ((WeatherByDayAdapter) adapter).setListener(new WeatherByDayAdapter.Listener() {
                    @Override
                    public void onClick(int position) {
                        CurrentDisplayedWeather currentDisplayedWeather = dataManager.getWeatherByDays().get(position);
                        dataManager.setCurrentDisplay(currentDisplayedWeather);
                        updateView(dataManager.getNameOfCity(), currentDisplayedWeather, dataManager.getLocation());
                    }
                });
                break;
            case R.id.byHour:
                adapter = new WeatherByHourAdapter(dataManager.getWeatherByHoursList(),sharedPreferences);
                break;
        }
        recycler.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false);
        recycler.setLayoutManager(manager);
    }

    public void onRadioButtonClicked(View view) {
        updateRecycler();
    }

    public void showMap(View view) {
        Uri location = Uri.parse("geo:0,0?q=" + dataManager.getLocation());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        startActivity(mapIntent);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public class GetWeatherAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            return getData(params[0]);

        }

        public String getData(String urlsite) {
            StringBuilder res = new StringBuilder("");
            InputStreamReader inputStream = null;
            try {
                URL url = new URL(urlsite);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10_000);
                conn.setConnectTimeout(15_000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                inputStream = new InputStreamReader(conn.getInputStream());
                char[] bytesBuffer = new char[1024];
                int read;
                while ((read = inputStream.read(bytesBuffer)) != -1) {
                    res.append(bytesBuffer, 0, read);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.connectionProblem), Toast.LENGTH_LONG).show();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return res.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject currently = jsonObject.getJSONObject("currently");
                Date date = new Date(currently.getLong("time") * 1000);
                Double temperature = currently.getDouble("temperature");
                String description = currently.getString("summary");
                Double humidity = currently.getDouble("humidity");
                Double wind = currently.getDouble("windSpeed");
                Double probability = currently.getDouble("precipProbability");
                int icon = getResources().getIdentifier(currently.getString("icon").replaceAll("-", ""), "drawable", "com.example.weather");
                CurrentDisplayedWeather currentDisplayedWeather = new CurrentDisplayedWeather(MainActivity.this, date, description, icon, temperature, wind, humidity, probability);
                dataManager.setCurrentDisplay(currentDisplayedWeather);
                updateView(dataManager.getNameOfCity(), currentDisplayedWeather, dataManager.getLocation());
                List<Weather> weatherByDayList = new ArrayList<>();
                List<Weather> weatherByHoursList = new ArrayList<>();
                JSONObject dailly = jsonObject.getJSONObject("daily");
                JSONArray daillArray = dailly.getJSONArray("data");
                for (int i = 0; i < daillArray.length(); i++) {
                    JSONObject data = daillArray.getJSONObject(i);
                    date = new Date(data.getLong("time") * 1000);
                    icon = getResources().getIdentifier(data.getString("icon").replaceAll("-", ""), "drawable", "com.example.weather");
                    Double maxT = data.getDouble("temperatureMax");
                    Double minT = data.getDouble("temperatureMin");
                    wind = data.getDouble("windSpeed");
                    humidity = data.getDouble("humidity");
                    probability = data.getDouble("precipProbability");
                    description = currently.getString("summary");
                    weatherByDayList.add(new WeatherByDay(MainActivity.this, date, description, icon, maxT, minT, wind, humidity, probability));
                }
                JSONObject hourly = jsonObject.getJSONObject("hourly");
                JSONArray hourlyArray = hourly.getJSONArray("data");
                for (int i = 0; i < hourlyArray.length(); i++) {
                    JSONObject data = hourlyArray.getJSONObject(i);
                    date = new Date(data.getLong("time") * 1000);
                    icon = getResources().getIdentifier(data.getString("icon").replaceAll("-", ""), "drawable", "com.example.weather");
                    wind = data.getDouble("windSpeed");
                    temperature = data.getDouble("temperature");
                    weatherByHoursList.add(new WeatherByHours(MainActivity.this, date, icon, temperature, wind));
                }
                dataManager.setWeatherByDays(weatherByDayList);
                dataManager.setWeatherByHoursList(weatherByHoursList);
                updateRecycler();
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }

    //this async task finds latitude, longitude by cityName in google maps and passes them to getWeatherAsyncTask
    public class GetLatLongAsyncTask extends
            AsyncTask<Void, Void, StringBuilder> {
        String place;

        public GetLatLongAsyncTask(String place) {
            super();
            this.place = place;

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            this.cancel(true);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            InputStreamReader in = null;
            try {
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
                        + this.place + "&sensor=false&language=ru";
                URL url = new URL(googleMapUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10_000);
                conn.setConnectTimeout(15_000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                in = new InputStreamReader(
                        conn.getInputStream());

                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                return jsonResults;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            if (result == null) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.unncorrect), Toast.LENGTH_LONG).show();
                return;
            }
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                JSONArray resultJsonArray = jsonObj.getJSONArray("results");
                JSONObject before_geometry_jsonObj = resultJsonArray.getJSONObject(0);
                JSONObject geometry_jsonObj = before_geometry_jsonObj.getJSONObject("geometry");
                JSONObject location_jsonObj = geometry_jsonObj.getJSONObject("location");
                String latitude = location_jsonObj.getString("lat");
                String longitude = location_jsonObj.getString("lng");
                dataManager.setLocation(before_geometry_jsonObj.getString("formatted_address"));
                if (longitude != null && longitude != null) {
                    if (checkConnection()) {
                        new GetWeatherAsyncTask().execute(getUrl(latitude, longitude));
                    }
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.unncorrect), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }

    public String getUrl(String latitude, String longitude) {
        StringBuilder prepareUrl = new StringBuilder("https://api.darksky.net/forecast/fff553af3244a00bd36d3c0b398dce88/");
        prepareUrl.append(latitude);
        prepareUrl.append(",");
        prepareUrl.append(longitude);
        prepareUrl.append("?units=si&lang=ru");
        return prepareUrl.toString();
    }

    //checks network connection depending on the settings specified by the user (only wifi or any connection)
    public boolean checkConnection() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.INTERNET},
                1);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        String onlyWiFi = sharedPreferences.getString(WIFI, null);
        if (onlyWiFi.equals("OFF") && networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else if (onlyWiFi.equals("ON")) {
            NetworkInfo networkInfoWiFi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected() && networkInfoWiFi != null && networkInfoWiFi.isConnected()) {
                return true;
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.connectionWifiProblem), Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.connectionProblem), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                final String message = "это разрешение необходимо что бы делится погодой";
                Snackbar.make(temperatureV, message, Snackbar.LENGTH_LONG)
                        .setAction("GRANT", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        }
        else {
            setIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setIntent();

                } else {
                    menuShareItem.setVisible(false);
                }
                return;
            }
        }
    }

}
