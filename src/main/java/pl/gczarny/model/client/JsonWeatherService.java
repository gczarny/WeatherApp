package pl.gczarny.model.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pl.gczarny.Config;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class JsonWeatherService {
    private final Gson gson = new Gson();
    public JsonObject getJsonObjectFromApi(String location) throws WeatherDataFetchException {
        HttpURLConnection con = null;
        try {
            URL url = new URL(String.format(Config.getForecastApiUrl(), URLEncoder.encode(location, StandardCharsets.UTF_8)));
            con = connectToApi(url);
            if (con.getResponseCode() != 200) {
                throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.http.response") + con.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
            String jsonData = "";
            String output;
            while ((output = br.readLine()) != null) {
                jsonData += output;
            }
            if(jsonData.isEmpty()){
                throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.http.response") + con.getResponseCode());
            }
            return gson.fromJson(jsonData, JsonObject.class);
        }catch (FileNotFoundException e) {
            throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.not.found"));
        }catch (IOException e) {
            throw new WeatherDataFetchException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        }finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
    private HttpURLConnection connectToApi(URL url) throws IOException {
        HttpURLConnection con;
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        return con;
    }
}
