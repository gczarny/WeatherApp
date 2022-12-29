package pl.gczarny.model;

import pl.gczarny.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherDataFetcher {

    public static double getTemperature(String location){
        try{
            String urlString = String.format(Config.getApiUrl(), location);
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            System.out.println("Pobieranie danych z serwera...");
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while((inputLine = reader.readLine()) != null){
                content.append(inputLine);
            }
            reader.close();
            con.disconnect();
            System.out.println("Dane zostały pobrane pomyślnie.");
            JsonObject json = new JsonParser().parse(content.toString()).getAsJsonObject();
            JsonObject main = json.getAsJsonObject("main");
            double temperatureInKelvins = main.get("temp").getAsDouble();
            return temperatureInKelvins  - 273.15;
        } catch (Exception e) {
            e.printStackTrace();
            return Double.NaN;
        }
    }
}
