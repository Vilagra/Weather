package com.example.weather;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
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
import com.example.weather.entity.WeatherByDay;
import com.example.weather.loaders.CoordinatesLoader;
import com.example.weather.loaders.WeatherLoader;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private ShareActionProvider shareActionProvider;
    private SharedPreferences sharedPreferences;
    private DataManager dataManager;
    LoaderManager.LoaderCallbacks<String[]> callbackForCoordinatesLoader;
    LoaderManager.LoaderCallbacks<String> callbackForWeatherLoader;


    public final String RADIOBUTTON_ID = "radoButtonId";
    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 75443;
    private final int SETTINGS_RESULT = 75444;
    private final int LOADER_WEATHER_ID=1;
    private final int LOADER_COORDINATES_ID=2;

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
        dataManager = new DataManager(getSharedPreferences(getString(R.string.myData), Context.MODE_PRIVATE));
        PreferenceManager.setDefaultValues(this,R.xml.pref,false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        callbackForCoordinatesLoader= new LoaderManager.LoaderCallbacks<String[]>() {
            @Override
            public Loader<String[]> onCreateLoader(int id, Bundle args) {
                return new CoordinatesLoader(MainActivity.this,args,dataManager);
            }
            @Override
            public void onLoadFinished(Loader<String[]> loader, String[] data) {
                if (data==null) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.unncorrect), Toast.LENGTH_LONG).show();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putStringArray(WeatherLoader.COORDINATES,data);
                    getLoaderManager().restartLoader(LOADER_WEATHER_ID,bundle,callbackForWeatherLoader).forceLoad();
                }
            }
            @Override
            public void onLoaderReset(Loader<String[]> loader) {}
        };
        callbackForWeatherLoader = new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, Bundle args) {
                return new WeatherLoader(MainActivity.this,args,dataManager);
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String data) {
                updateView(dataManager.getNameOfCity(), dataManager.getCurrentDisplay(), dataManager.getLocation());
                updateRecycler();
            }
            @Override
            public void onLoaderReset(Loader<String> loader) {}
        };
        Bundle bundle= new Bundle();
        bundle.putString(CoordinatesLoader.CITY_NAME,"");
        String[] s = {""};
        bundle.putStringArray(WeatherLoader.COORDINATES,s);
        getLoaderManager().initLoader(LOADER_COORDINATES_ID,bundle,callbackForCoordinatesLoader);
        getLoaderManager().initLoader(LOADER_WEATHER_ID,bundle,callbackForWeatherLoader);
        if (dataManager.getCurrentDisplay() != null) {
            updateView(dataManager.getNameOfCity(), dataManager.getCurrentDisplay(), dataManager.getLocation());
            if (savedInstanceState != null) {
                RadioButton radioButton = (RadioButton) findViewById(savedInstanceState.getInt(RADIOBUTTON_ID));
                radioButton.setChecked(true);
            }
            updateRecycler();
        }
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
    public void refreshData() {
        if (checkConnection()) {
            Bundle bundle = new Bundle();
            bundle.putString(CoordinatesLoader.CITY_NAME,enterCity.getText().toString().replaceAll(" ", "%20"));
            //Log.d()
            Loader<String[]> loader= getLoaderManager().restartLoader(LOADER_COORDINATES_ID,bundle,callbackForCoordinatesLoader);
            loader.forceLoad();
            //new GetLatLongAsyncTask().execute(enterCity.getText().toString().replaceAll(" ", "%20"));
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RADIOBUTTON_ID, radioGroupV.getCheckedRadioButtonId());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.settings).setIntent(new Intent(this,SettingsActivity.class));
        menuShareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) menuShareItem.getActionProvider();
        checkPermission();
        return super.onCreateOptionsMenu(menu);
    }

    private void setIntent() {
        Bitmap bitmap = getBitmapFromView((LinearLayout) findViewById(R.id.main));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, getImageUri(this, bitmap));
        shareActionProvider.setShareIntent(intent);
    }

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


    private void updateView(String city, CurrentDisplayedWeather currentDisplayedWeather, String location) {
        if (city.length() > 0) {
            city = city.substring(0, 1).toUpperCase() + city.substring(1);
        }
        cityV.setText(city);
        temperatureV.setText(currentDisplayedWeather.getTemperatureString(sharedPreferences.getString(getString(R.string.temperature), null)));
        locationV.setText(location);
        descriptionV.setText(currentDisplayedWeather.getSummary());
        humidityV.setText(getString(R.string.humidity) + " " + currentDisplayedWeather.getHumidityString());
        windV.setText(getString(R.string.wind_speed) + " " + currentDisplayedWeather.getWindString(sharedPreferences.getString(getString(R.string.wind_speed), null)));
        pictureV.setImageResource(currentDisplayedWeather.getIdDrawable(getBaseContext()));
        probabilityV.setText(getString(R.string.probability) + " " + currentDisplayedWeather.getPrecipProbString());
        dayOfWeekV.setText(currentDisplayedWeather.getDayOfWeekLong());
        linkV.setText(Html.fromHtml("<a href=\"https://icons8.com/web-app/3350/Clouds\">Clouds icon credits</a>"));
        linkV.setMovementMethod(LinkMovementMethod.getInstance());
        if (shareActionProvider != null) {
            checkPermission();
        }
    }


    private void updateRecycler() {
        int id = radioGroupV.getCheckedRadioButtonId();
        RecyclerView.Adapter adapter = null;
        switch (id) {
            case R.id.byDay:
                adapter = new WeatherByDayAdapter(getApplicationContext(),dataManager.getWeatherByDays(), sharedPreferences);
                ((WeatherByDayAdapter) adapter).setListener(new WeatherByDayAdapter.Listener() {
                    @Override
                    public void onClick(int position) {
                        WeatherByDay weatherByDay=dataManager.getWeatherByDays().get(position);
                        CurrentDisplayedWeather currentDisplayedWeather = weatherByDay;
                        currentDisplayedWeather.setTemperature(weatherByDay.getMaxT());
                        dataManager.setCurrentDisplay(currentDisplayedWeather);
                        updateView(dataManager.getNameOfCity(), currentDisplayedWeather, dataManager.getLocation());
                    }
                });
                break;
            case R.id.byHour:
                adapter = new WeatherByHourAdapter(getApplicationContext(),dataManager.getWeatherByHoursList(), sharedPreferences);
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



    //this async task finds latitude, longitude by cityName in google maps and passes them to getWeatherAsyncTask
    public class GetLatLongAsyncTask extends
            AsyncTask<String, Void, String[]> {

        @Override
        protected void onCancelled() {
            super.onCancelled();
            this.cancel(true);
        }

        @Override
        protected String[] doInBackground(String... params) {
            InputStreamReader in = null;
            String place = params[0];
            Log.e("CITY", Arrays.toString(params));
            try {
                StringBuilder result = new StringBuilder();
                String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
                        + place + "&sensor=false&language=ru";
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
                    result.append(buff, 0, read);
                }
                if (result == null) {
                    return null;
                }
                JSONObject jsonObj = new JSONObject(result.toString());
                Log.d("jsonLOc",jsonObj.toString());
                JSONArray resultJsonArray = jsonObj.getJSONArray("results");
                JSONObject before_geometry_jsonObj = resultJsonArray.getJSONObject(0);
                JSONObject geometry_jsonObj = before_geometry_jsonObj.getJSONObject("geometry");
                JSONObject location_jsonObj = geometry_jsonObj.getJSONObject("location");
                String latitude = location_jsonObj.getString("lat");
                String longitude = location_jsonObj.getString("lng");
                dataManager.setLocation(before_geometry_jsonObj.getString("formatted_address"));
                dataManager.setNameOfCity(dataManager.getLocation().split(",")[0]);
                if (longitude != null && latitude != null) {
                    String[] strings = new String[2];
                    strings[0]=longitude;
                    strings[1]=latitude;
                    return strings;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            if (s==null) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.unncorrect), Toast.LENGTH_LONG).show();
            }
            else {
                //new GetWeatherAsyncTask().execute(s);
        }
    }

}

    //checks network connection depending on the settings specified by the user (only wifi or any connection)
    public boolean checkConnection() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.INTERNET},
                1);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Boolean onlyWiFi = sharedPreferences.getBoolean(getString(R.string.wifi), false);
        if (!onlyWiFi && networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else if (onlyWiFi) {
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
        } else {
            setIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(String.valueOf(RESULT_OK), String.valueOf(resultCode));
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SETTINGS_RESULT:
                    updateView(dataManager.getNameOfCity(), dataManager.getCurrentDisplay(), dataManager.getLocation());
                    updateRecycler();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
       sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateView(dataManager.getNameOfCity(), dataManager.getCurrentDisplay(), dataManager.getLocation());
        updateRecycler();
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
