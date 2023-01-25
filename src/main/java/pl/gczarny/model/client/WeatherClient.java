package pl.gczarny.model.client;

import pl.gczarny.model.WeatherData;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import java.util.List;

public interface WeatherClient {
    List<WeatherData> fetchForecastData(String location) throws WeatherDataFetchException;
}
