package pl.gczarny.model;

import javafx.concurrent.Task;

import java.util.List;

public class ForecastDataFetchTask extends Task<List<WeatherData>> {
    private String location;

    public ForecastDataFetchTask(String location) {
        this.location = location;
    }

    @Override
    protected List<WeatherData> call() throws Exception {
        return WeatherDataFetcher.fetchForecastData(location);
    }
}
