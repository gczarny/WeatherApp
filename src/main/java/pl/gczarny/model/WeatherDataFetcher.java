package pl.gczarny.model;

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
import com.google.gson.JsonParser;
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
            long timestamp = forecast.get("dt").getAsLong();
            String timestamp_txt = forecast.get("dt_txt").getAsString();
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
            if (dateTime.toLocalDate().isEqual(LocalDate.now()) && forecastList.size() == 0 ||
                    (dateTime.toLocalDate().isAfter(LocalDate.now()) && timestamp_txt.endsWith("12:00:00"))) {
                forecastList.add(new WeatherData(getTemperature(forecast), getPressure(forecast),
                                                getWindSpeed(forecast), getWindDeg(forecast), getHumidity(forecast),
                                                getWeatherIcon(forecast), getLocation(jsonCityObject), dateTime,
                                                getPopulation(jsonCityObject), getWeatherId(forecast)));
            }
            if(forecastList.size() == 5){
                break;
            }
        }
        return forecastList;
    }

    public static double getTemperature(JsonObject json){
        JsonObject main = json.getAsJsonObject("main");
        return main.get("temp").getAsDouble();
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

    public static double getHumidity(JsonObject json){
        JsonObject main = json.getAsJsonObject("main");
        double humidity = main.get("humidity").getAsDouble();
        return humidity;
    }
    public static double getPressure(JsonObject json){
        JsonObject main = json.getAsJsonObject("main");
        double pressure = main.get("pressure").getAsDouble();
        return pressure;
    }
    public static double getWindSpeed(JsonObject json){
        JsonObject main = json.getAsJsonObject("wind");
        double speed = main.get("speed").getAsDouble();
        return speed;
    }
    public static double getWindDeg(JsonObject json){
        JsonObject main = json.getAsJsonObject("wind");
        double deg = main.get("deg").getAsDouble();
        return deg;
    }
    public static int getPopulation(JsonObject json){
        int population = json.get("population").getAsInt();
        return population;
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
