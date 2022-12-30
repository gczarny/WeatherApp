package pl.gczarny.model;

import com.google.gson.JsonArray;
import pl.gczarny.Config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pl.gczarny.utils.DialogUtils;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

public class WeatherDataFetcher {

    public static WeatherData fetchWeatherData(String location) throws WeatherDataFetchException {
        JsonObject json = fetchWeatherDataFromApi(location);
        double temperature = getTemperature(json);
        String description = getWeatherDescription(json);
        return new WeatherData(temperature, description);
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
    private static JsonObject fetchWeatherDataFromApi(String location) throws WeatherDataFetchException {
        try{
            String urlString = String.format(Config.getApiUrl(), location);
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
            //return Double.NaN;
        } catch (Exception e) {
            throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
            //return Double.NaN;
        }
    }
}
