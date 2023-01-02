package pl.gczarny.model;

import javafx.concurrent.Task;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import java.util.List;

public class WeatherDataFetchTask extends Task<List<WeatherData>> {
    private String location;

    public WeatherDataFetchTask(String location) {
        this.location = location;
    }

    @Override
    protected List<WeatherData> call() throws Exception {
        return WeatherDataFetcher.fetchForecastData(location);
    }

}
