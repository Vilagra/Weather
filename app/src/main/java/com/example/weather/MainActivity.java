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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ShareActionProvider shareActionProvider;
    private SharedPreferences sharedPreferences;
    private DataManager dataManager;
    LoaderManager.LoaderCallbacks<String[]> callbackForCoordinatesLoader;
    LoaderManager.LoaderCallbacks<String> callbackForWeatherLoader;


    public final String RADIOBUTTON_ID = "radoButtonId";
    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 75443;
    private final int LOADER_WEATHER_ID = 1;
    private final int LOADER_COORDINATES_ID = 2;
    @BindView(R.id.enterCity)
    EditText enterCity;
    @BindView(R.id.city)
    TextView cityV;
    @BindView(R.id.textviewTemperature)
    TextView temperatureV;
    @BindView(R.id.description)
    TextView descriptionV;
    @BindView(R.id.city_full_name)
    TextView locationV;
    @BindView(R.id.full_day_of_week)
    TextView dayOfWeekV;
    @BindView(R.id.picture)
    ImageView pictureV;
    @BindView(R.id.humidity)
    TextView humidityV;
    @BindView(R.id.wind)
    TextView windV;
    @BindView(R.id.probability)
    TextView probabilityV;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.radio_group)
    RadioGroup radioGroupV;
    @BindView(R.id.link)
    TextView linkV;
    MenuItem menuShareItem;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dataManager = new DataManager(getSharedPreferences(getString(R.string.myData), Context.MODE_PRIVATE));
        PreferenceManager.setDefaultValues(this, R.xml.pref, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        callbackForCoordinatesLoader = new LoaderManager.LoaderCallbacks<String[]>() {
            @Override
            public Loader<String[]> onCreateLoader(int id, Bundle args) {
                return new CoordinatesLoader(MainActivity.this, args, dataManager);
            }

            @Override
            public void onLoadFinished(Loader<String[]> loader, String[] data) {
                if (data == null) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.unncorrect), Toast.LENGTH_LONG).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putStringArray(WeatherLoader.COORDINATES, data);
                    getLoaderManager().restartLoader(LOADER_WEATHER_ID, bundle, callbackForWeatherLoader).forceLoad();
                }
            }

            @Override
            public void onLoaderReset(Loader<String[]> loader) {
            }
        };
        callbackForWeatherLoader = new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, Bundle args) {
                return new WeatherLoader(MainActivity.this, args, dataManager);
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String data) {
                updateView(dataManager.getNameOfCity(), dataManager.getCurrentDisplay(), dataManager.getLocation());
                updateRecycler();
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {
            }
        };
        Bundle bundle = new Bundle();
        bundle.putString(CoordinatesLoader.CITY_NAME, "");
        String[] s = {""};
        bundle.putStringArray(WeatherLoader.COORDINATES, s);
        getLoaderManager().initLoader(LOADER_COORDINATES_ID, bundle, callbackForCoordinatesLoader);
        getLoaderManager().initLoader(LOADER_WEATHER_ID, bundle, callbackForWeatherLoader);
        if (dataManager.getCurrentDisplay() != null) {
            updateView(dataManager.getNameOfCity(), dataManager.getCurrentDisplay(), dataManager.getLocation());
            if (savedInstanceState != null) {
                RadioButton radioButton = (RadioButton) findViewById(savedInstanceState.getInt(RADIOBUTTON_ID));
                radioButton.setChecked(true);
            }
            updateRecycler();
        }


    }

    @OnClick(R.id.buttonRefresh)
    public void onClick() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //these rows are present in order to hide keyboard
        imm.hideSoftInputFromWindow(enterCity.getWindowToken(), 0);                                  // when button "найти" is pressed
        refreshData();
    }


    public void refreshData() {
        if (checkConnection()) {
            Bundle bundle = new Bundle();
            bundle.putString(CoordinatesLoader.CITY_NAME, enterCity.getText().toString().replaceAll(" ", "%20"));
            Loader<String[]> loader = getLoaderManager().restartLoader(LOADER_COORDINATES_ID, bundle, callbackForCoordinatesLoader);
            loader.forceLoad();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RADIOBUTTON_ID, radioGroupV.getCheckedRadioButtonId());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.settings).setIntent(new Intent(this, SettingsActivity.class));
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
                adapter = new WeatherByDayAdapter(getApplicationContext(), dataManager.getWeatherByDays(), sharedPreferences);
                ((WeatherByDayAdapter) adapter).setListener(new WeatherByDayAdapter.Listener() {
                    @Override
                    public void onClick(int position) {
                        WeatherByDay weatherByDay = dataManager.getWeatherByDays().get(position);
                        CurrentDisplayedWeather currentDisplayedWeather = weatherByDay;
                        currentDisplayedWeather.setTemperature(weatherByDay.getMaxT());
                        dataManager.setCurrentDisplay(currentDisplayedWeather);
                        updateView(dataManager.getNameOfCity(), currentDisplayedWeather, dataManager.getLocation());
                    }
                });
                break;
            case R.id.byHour:
                adapter = new WeatherByHourAdapter(getApplicationContext(), dataManager.getWeatherByHoursList(), sharedPreferences);
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
