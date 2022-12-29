package pl.gczarny.model;

import javafx.concurrent.Task;

public class WeatherDataFetchTask extends Task<WeatherData> {

    private String location;

    public WeatherDataFetchTask(String location) {
        this.location = location;
    }

    @Override
    protected WeatherData call() throws Exception {
        return WeatherDataFetcher.fetchWeatherData(location);
    }
}
