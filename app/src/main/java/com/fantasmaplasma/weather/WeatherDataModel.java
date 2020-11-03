package com.fantasmaplasma.weather;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataModel {

    private String mCity;
    private String mIconName;
    private int mCondition;
    private double mCelsiusTemperature;

    static String CELSIUS = "°C";
    static String FAHRENHEIT = "°F";
    static String EXTRA_CITY = "City";

    public static WeatherDataModel fromJson(JSONObject jsonObject) {
        WeatherDataModel weatherData = new WeatherDataModel();
        try {
            weatherData.mCity = jsonObject.getString("name");
            weatherData.mCondition = jsonObject.getJSONArray("weather")
                    .getJSONObject(0).getInt("id");
            weatherData.mIconName = updateWeatherIcon(weatherData.mCondition);

            weatherData.mCelsiusTemperature = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;

            return weatherData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String updateWeatherIcon(int condition) {
        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }
        return "dunno";
    }

    public String getCity() {
        return mCity;
    }

    public String getTemperatureCelsius() {
        return (int)Math.rint(mCelsiusTemperature) + CELSIUS;
    }

    public String getTemperatureFahrenheit() {
        return (int)Math.rint(mCelsiusTemperature/5*9+32) + FAHRENHEIT;
    }

    public String getIconName() {
        return mIconName;
    }
}
