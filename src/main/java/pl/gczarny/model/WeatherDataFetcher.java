package pl.gczarny.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import pl.gczarny.Config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

public class WeatherDataFetcher {

    public static List<WeatherData> fetchForecastData(String location) throws WeatherDataFetchException {
        JsonObject fetchedJsonData = getJsonObjectFromApi(location);
        JsonArray jsonForecastArray = fetchedJsonData.getAsJsonArray("list");
        JsonObject jsonCityObject = fetchedJsonData.getAsJsonObject("city");
        List<WeatherData> forecastList = new ArrayList<WeatherData>();
        for(int i = 0; i < jsonForecastArray.size(); i++){
            JsonObject forecast = jsonForecastArray.get(i).getAsJsonObject();
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(forecast.get("dt").getAsLong()), ZoneId.systemDefault());
            if(shouldAddForecastData(forecastList, dateTime, forecast.get("dt_txt").getAsString()))
            {
                forecastList.add(getWeatherData(forecast, jsonCityObject, dateTime));
            }
            if(forecastList.size() == 5){
                break;
            }
        }
        return forecastList;
    }

    public static String getWeatherIcon(JsonObject json){
        JsonArray weatherArray = json.getAsJsonArray("weather");
        JsonObject weather = weatherArray.get(0).getAsJsonObject();
        return weather.get("icon").getAsString();
    }
    public static int getWeatherId(JsonObject json){
        JsonArray weatherArray = json.getAsJsonArray("weather");
        JsonObject id = weatherArray.get(0).getAsJsonObject();
        return id.get("id").getAsInt();
    }

    public static double getTemperature(JsonObject json){
        return getDoubleWeatherDataFromJsonObject(json, "main", "temp");
    }

    public static double getHumidity(JsonObject json){
        return getDoubleWeatherDataFromJsonObject(json, "main", "humidity");
    }
    public static double getPressure(JsonObject json){
        return getDoubleWeatherDataFromJsonObject(json, "main", "pressure");
    }
    public static double getWindSpeed(JsonObject json){
        return getDoubleWeatherDataFromJsonObject(json, "wind", "speed");
    }
    public static double getWindDeg(JsonObject json){
        return getDoubleWeatherDataFromJsonObject(json, "wind", "deg");
    }
    public static int getPopulation(JsonObject json){
        return json.get("population").getAsInt();
    }
    public static String getLocation(JsonObject json){
        String cityName = json.get("name").getAsString();
        return new String(cityName.getBytes(), StandardCharsets.UTF_8);
    }
    private static JsonObject getJsonObjectFromApi(String location) throws WeatherDataFetchException {
        try{
            String urlString = String.format(Config.getForecastApiUrl(), URLEncoder.encode(location, StandardCharsets.UTF_8.toString()));
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            if (con.getResponseCode() != 200) {
                throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.http.response") + con.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
            String jsonData = "";
            String output;
            while ((output = br.readLine()) != null) {
                jsonData += output;
            }
            con.disconnect();
            return new Gson().fromJson(jsonData, JsonObject.class);
        }catch (FileNotFoundException e) {
            throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.not.found"));


        } catch (Exception e) {
            throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        }
    }
    private static double getDoubleWeatherDataFromJsonObject(JsonObject jsonObject, String memberName, String data){
        JsonObject json = jsonObject.getAsJsonObject(memberName);
        double returnData = json.get(data).getAsDouble();
        return returnData;
    }

    private static boolean shouldAddForecastData(List<WeatherData> forecastList, LocalDateTime dateTime, String timestmp){
        return dateTime.toLocalDate().isEqual(LocalDate.now()) && forecastList.size() == 0 ||
                (dateTime.toLocalDate().isAfter(LocalDate.now()) && timestmp.endsWith("12:00:00"));
    }

    private static WeatherData getWeatherData(JsonObject forecast, JsonObject jsonCityObject, LocalDateTime dateTime){
        return new WeatherData(getTemperature(forecast), getPressure(forecast),
                getWindSpeed(forecast), getWindDeg(forecast), getHumidity(forecast),
                getWeatherIcon(forecast), getLocation(jsonCityObject), dateTime,
                getPopulation(jsonCityObject), getWeatherId(forecast));
    }
}
