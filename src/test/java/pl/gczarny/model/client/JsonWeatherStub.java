package pl.gczarny.model.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class JsonWeatherStub {
    public static JsonObject getWeatherFromJson() {
        String stringJson = null;
        try {
            stringJson = new String(Files.readAllBytes(Path.of("src/test/resources/forecastWeather.json")));
            JsonElement jsonElement = JsonParser.parseString(stringJson);
            JsonArray listArray = jsonElement.getAsJsonObject().getAsJsonArray("list");
            // get the current date and time
            LocalDateTime currentDateTime = LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime nextDateTime = currentDateTime;
            // loop through the next 5 elements in the list
            for (int i = 1; i < listArray.size()+1; i++) {
                JsonObject element = listArray.get(i-1).getAsJsonObject();

                // calculate the next 3-hour interval and update the dt and dt_txt values
                element.addProperty("dt", nextDateTime.toEpochSecond(ZoneOffset.UTC));
                element.addProperty("dt_txt", nextDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                nextDateTime = currentDateTime.plusHours(i * 3);
            }
            return jsonElement.getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static JsonObject getEmptyJsonFile(){
        String stringJson = null;
        try {
            stringJson = new String(Files.readAllBytes(Path.of("src/test/resources/emptyJsonFile.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonElement jsonElement = JsonParser.parseString(stringJson);
        return jsonElement.getAsJsonObject();
    }
}

