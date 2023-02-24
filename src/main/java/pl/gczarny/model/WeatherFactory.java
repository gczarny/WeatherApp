package pl.gczarny.model;

import pl.gczarny.model.client.JsonWeatherService;
import pl.gczarny.model.client.OpenWeatherMapApiFetcher;
import pl.gczarny.model.client.WeatherClient;

public class WeatherFactory {
    public WeatherClient createWeatherClient(){
        return new OpenWeatherMapApiFetcher(new JsonWeatherService());
    }
}
