package pl.gczarny.model.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.nio.charset.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import pl.gczarny.model.WeatherData;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

public class OpenWeatherMapApiFetcher implements WeatherClient {
    private final List<WeatherData> forecastList = new ArrayList<WeatherData>();
    private final ZoneId zoneId = ZoneId.systemDefault();
    private JsonWeatherService jsonWeatherService;

    public OpenWeatherMapApiFetcher(JsonWeatherService jsonWeatherService) {
        this.jsonWeatherService = jsonWeatherService;
    }

    @Override
    public List<WeatherData> fetchForecastData(String location) throws WeatherDataFetchException {
        JsonObject jsonObjectFromApi = jsonWeatherService.getJsonObjectFromApi(location);

        if(jsonObjectFromApi.isJsonNull() || jsonObjectFromApi.size() == 0){
            throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.response.empty"));
        }
        for(JsonElement element : jsonObjectFromApi.getAsJsonArray("list")){
            //System.out.println(getLDT(element.getAsJsonObject().get("dt").getAsLong()));
            //System.out.println(element.getAsJsonObject().get("dt_txt").getAsString());
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
        JsonObject weatherObject = weatherArray.get(0).getAsJsonObject();
        if (weatherObject.has("icon")) {
            return weatherObject.get("icon").getAsString();
        } else {
            return "empty";
        }
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
