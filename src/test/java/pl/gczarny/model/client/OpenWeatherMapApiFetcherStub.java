package pl.gczarny.model.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

public class OpenWeatherMapApiFetcherStub {
    public static JsonObject getJsonObjectFromApi() {
        JsonObject jsonObject = new JsonObject();

        JsonObject city = new JsonObject();
        city.addProperty("name", "Warsaw");
        city.addProperty("population", 1702132);

        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < 5; i++) {
            JsonObject jsonForecast = new JsonObject();

            LocalDateTime localDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 24, 12, 0);
            long epoch = localDateTime.toEpochSecond(ZoneOffset.UTC);

            jsonForecast.addProperty("dt", epoch);
            jsonForecast.addProperty("dt_txt", localDateTime.toString());

            JsonObject main = new JsonObject();
            main.addProperty("temp", 1.5);
            main.addProperty("pressure", 1025.5);
            main.addProperty("humidity", 60.5);
            jsonForecast.add("main", main);

            JsonObject wind = new JsonObject();
            wind.addProperty("speed", 3.5);
            wind.addProperty("deg", 220.5);
            jsonForecast.add("wind", wind);

            JsonArray weatherArray = new JsonArray();
            JsonObject weatherObject = new JsonObject();
            weatherObject.addProperty("icon", "01d");
            weatherObject.addProperty("id", 800);
            weatherArray.add(weatherObject);
            jsonForecast.add("weather", weatherArray);

            jsonArray.add(jsonForecast);
        }
        jsonObject.add("list", jsonArray);
        jsonObject.add("city", city);

        return jsonObject;
    }
}

