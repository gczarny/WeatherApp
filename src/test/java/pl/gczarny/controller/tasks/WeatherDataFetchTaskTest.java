package pl.gczarny.controller.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.gczarny.model.client.JsonWeatherService;
import pl.gczarny.model.client.OpenWeatherMapApiFetcher;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class WeatherDataFetchTaskTest {

    private OpenWeatherMapApiFetcher openWeatherMapApiFetcher;
    private JsonWeatherService mockJsonWeatherService;

    @BeforeEach
    public void setUp() {
        mockJsonWeatherService = mock(JsonWeatherService.class);
        openWeatherMapApiFetcher = new OpenWeatherMapApiFetcher(mockJsonWeatherService);
    }

    @Test
    public void incorrectLocationShouldThrowWeatherDataFetchException() {
        // given
        String location = "NonexistingCity";
        WeatherDataFetchTask weatherDataFetchTask = new WeatherDataFetchTask(location);

        // then
        assertThrows(WeatherDataFetchException.class, () -> {
            weatherDataFetchTask.call();
        });
    }

}