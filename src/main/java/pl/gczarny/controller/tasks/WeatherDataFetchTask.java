package pl.gczarny.controller.tasks;

import javafx.concurrent.Task;
import pl.gczarny.model.WeatherData;
import pl.gczarny.model.WeatherFactory;
import pl.gczarny.model.client.WeatherClient;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import java.util.List;

public class WeatherDataFetchTask extends Task<List<WeatherData>> {
    private final String location;

    public WeatherDataFetchTask(String location) {
        this.location = location;
    }

    @Override
    protected List<WeatherData> call() throws WeatherDataFetchException {
        WeatherClient weatherClient = new WeatherFactory().createWeatherClient();
        return weatherClient.fetchForecastData(location);
    }
}
