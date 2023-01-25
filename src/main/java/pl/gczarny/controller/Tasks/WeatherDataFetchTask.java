package pl.gczarny.controller.Tasks;

import javafx.concurrent.Task;
import pl.gczarny.model.WeatherData;
import pl.gczarny.model.WeatherFactory;
import pl.gczarny.model.client.OpenWeatherMapApiFetcher;
import pl.gczarny.model.client.WeatherClient;

import java.util.List;

public class WeatherDataFetchTask extends Task<List<WeatherData>> {
    private String location;

    public WeatherDataFetchTask(String location) {
        this.location = location;
    }

    @Override
    protected List<WeatherData> call() throws Exception {
        WeatherClient weatherClient = new WeatherFactory().createWeatherClient();
        return weatherClient.fetchForecastData(location);
    }
}
