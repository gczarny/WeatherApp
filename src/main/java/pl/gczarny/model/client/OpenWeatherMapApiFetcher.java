package pl.gczarny.model.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import pl.gczarny.model.WeatherData;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

public class OpenWeatherMapApiFetcher implements WeatherClient{
    private final List<WeatherData> forecastList = new ArrayList<WeatherData>();
    private final Gson gson = new Gson();
    private final ZoneId zoneId = ZoneId.systemDefault();

    @Override
    public List<WeatherData> fetchForecastData(String location) throws WeatherDataFetchException {
        JsonObject jsonObjectFromApi = getJsonObjectFromApi(location);
        for(JsonElement element : jsonObjectFromApi.getAsJsonArray("list")){
            if(shouldAddForecastData(forecastList, getLDT(element.getAsJsonObject().get("dt").getAsLong()),
                    element.getAsJsonObject().get("dt_txt").getAsString()))
            {
                forecastList.add(getWeatherData(element.getAsJsonObject(), jsonObjectFromApi.getAsJsonObject("city"),
                        getLDT(element.getAsJsonObject().get("dt").getAsLong())));
            }
            if(forecastList.size() == 5){
                break;
            }
        }
        return forecastList;
    }
    public String getWeatherIcon(JsonObject json){
        JsonArray weatherArray = json.getAsJsonArray("weather");
        return weatherArray.get(0).getAsJsonObject().get("icon").getAsString();
    }
    public int getWeatherId(JsonObject json){
        JsonArray weatherArray = json.getAsJsonArray("weather");
        return weatherArray.get(0).getAsJsonObject().get("id").getAsInt();
    }

    public double getTemperature(JsonObject json){
        return getDoubleWeatherDataFromJsonObject(json, "main", "temp");
    }

    public double getHumidity(JsonObject json){
        return getDoubleWeatherDataFromJsonObject(json, "main", "humidity");
    }
    public double getPressure(JsonObject json){
        return getDoubleWeatherDataFromJsonObject(json, "main", "pressure");
    }
    public double getWindSpeed(JsonObject json){
        return getDoubleWeatherDataFromJsonObject(json, "wind", "speed");
    }
    public double getWindDeg(JsonObject json){
        return getDoubleWeatherDataFromJsonObject(json, "wind", "deg");
    }
    public int getPopulation(JsonObject json){
        return json.get("population").getAsInt();
    }
    public String getLocation(JsonObject json){
        return new String(json.get("name").getAsString().getBytes(), StandardCharsets.UTF_8);
    }
    private JsonObject getJsonObjectFromApi(String location) throws WeatherDataFetchException {
        try{
            String urlString = String.format(Config.getForecastApiUrl(), URLEncoder.encode(location, StandardCharsets.UTF_8));
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
            return gson.fromJson(jsonData, JsonObject.class);
        }catch (FileNotFoundException e) {
            throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.not.found"));
        } catch (Exception e) {
            throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        }
    }
    private double getDoubleWeatherDataFromJsonObject(JsonObject jsonObject, String memberName, String data){
        return jsonObject.getAsJsonObject(memberName).get(data).getAsDouble();
    }

    private boolean shouldAddForecastData(List<WeatherData> forecastList, LocalDateTime dateTime, String timestmp){
        return dateTime.toLocalDate().isEqual(LocalDate.now()) && forecastList.size() == 0 ||
                (dateTime.toLocalDate().isAfter(LocalDate.now()) && timestmp.endsWith("12:00:00"));
    }
    private LocalDateTime getLDT(long dt){
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(dt), zoneId);
    }
    private WeatherData getWeatherData(JsonObject forecast, JsonObject jsonCityObject, LocalDateTime dateTime){
        return new WeatherData(getTemperature(forecast), getPressure(forecast),
                getWindSpeed(forecast), getWindDeg(forecast), getHumidity(forecast),
                getWeatherIcon(forecast), getLocation(jsonCityObject), dateTime,
                getPopulation(jsonCityObject), getWeatherId(forecast));
    }
}
