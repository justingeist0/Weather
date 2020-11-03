package com.fantasmaplasma.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    final int REQUEST_CODE = 123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    final String APP_ID = "1d61c9dcf08067e6d19a51bb53868b4f";
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;

    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);

        findViewById(R.id.changeCityButton)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent changeCityIntent = new Intent(WeatherController.this, ChangeCityController.class);
                    startActivity(changeCityIntent);
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String city = getIntent().getStringExtra("City");
        if(city != null) {
            getWeatherForNewCity(city);
        } else {
            getWeatherForCurrentLocation();
        }
    }

    private void getWeatherForNewCity(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        requestDataFromWeatherAPI(params);
    }

    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NotNull Location location) {
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                requestDataFromWeatherAPI(params);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(@NotNull String provider) { }
            @Override
            public void onProviderDisabled(@NotNull String provider) { }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherForCurrentLocation();
            }
        }
    }

    private void requestDataFromWeatherAPI(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
               if(weatherData == null) {
                   Toast.makeText(WeatherController.this, "Error parsing data.", Toast.LENGTH_SHORT).show();
               } else {
                   updateUI(weatherData);
               }
           }

           @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
               Toast.makeText(WeatherController.this, "Request Failed", Toast.LENGTH_SHORT).show();
           }
        });
    }

    private void updateUI(WeatherDataModel weather) {
        mTemperatureLabel.setText(weather.getTemperature());
        mCityLabel.setText(weather.getCity());

        int resourceID = getResources().getIdentifier(weather.getIconName(), "drawable", getPackageName());
        mWeatherImage.setImageResource(resourceID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager != null) mLocationManager.removeUpdates(mLocationListener);
    }
}
