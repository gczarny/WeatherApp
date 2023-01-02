package pl.gczarny.model;

import com.google.gson.JsonArray;
import pl.gczarny.Config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

public class WeatherDataFetcher {

    public static List<WeatherData> fetchForecastData(String location) throws WeatherDataFetchException {
        JsonObject jsonFetchedWeatherDataFromApi = fetchForecastWeatherDataFromApi(location);
        JsonArray jsonForecastArray = jsonFetchedWeatherDataFromApi.getAsJsonArray("list");
        List<WeatherData> forecastList = new ArrayList<WeatherData>();
        for(int i = 0; i < jsonForecastArray.size(); i++){
            JsonObject forecast = jsonForecastArray.get(i).getAsJsonObject();
            long timestamp = forecast.get("dt").getAsLong();
            String timestamp_txt = forecast.get("dt_txt").getAsString();
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
            if (timestamp_txt.endsWith("12:00:00")) {
                double temperature = getTemperature(forecast);
                String id = getWeatherId(forecast);
                forecastList.add(new WeatherData(temperature, location, id, dateTime));
            }
            if(forecastList.size() == 5){
                break;
            }
        }
        return forecastList;
    }

    public static double getTemperature(JsonObject json){
        //JsonObject json = fetchWeatherDataFromApi(location);
        JsonObject main = json.getAsJsonObject("main");
        double temperatureInKelvins = main.get("temp").getAsDouble();
        return temperatureInKelvins  - 273.15;
    }

    public static String getWeatherDescription(JsonObject json){
        //JsonObject json = fetchWeatherDataFromApi(location);
        JsonArray weatherArray = json.getAsJsonArray("weather");
        JsonObject weather = weatherArray.get(0).getAsJsonObject();
        return weather.get("description").getAsString();
    }
    public static String getWeatherId(JsonObject json){
        //JsonObject json = fetchWeatherDataFromApi(location);
        JsonArray weatherArray = json.getAsJsonArray("weather");
        JsonObject weather = weatherArray.get(0).getAsJsonObject();
        return weather.get("icon").getAsString();
    }
    private static JsonObject fetchForecastWeatherDataFromApi(String location) throws WeatherDataFetchException {
        try{
            String urlString = String.format(Config.getForecastApiUrl(), location);
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while((inputLine = reader.readLine()) != null){
                content.append(inputLine);
            }
            reader.close();
            con.disconnect();
            JsonObject json = new JsonParser().parse(content.toString()).getAsJsonObject();
            return json;
        }catch (FileNotFoundException e) {
            throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.not.found"));

        } catch (Exception e) {
            throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        }
    }
}
