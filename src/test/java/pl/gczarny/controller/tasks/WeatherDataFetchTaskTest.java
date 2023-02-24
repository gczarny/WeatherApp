package pl.gczarny.controller.tasks;

import org.junit.jupiter.api.Test;
import pl.gczarny.model.WeatherData;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeatherDataFetchTaskTest {

    @Test
    public void weatherDataListIsNotNullAfterCall() throws Exception {
        // given
        String location = "Warszawa";
        WeatherDataFetchTask weatherDataFetchTask = new WeatherDataFetchTask(location);

        // when
        List<WeatherData> weatherDataList = weatherDataFetchTask.call();

        // then
        assertNotNull(weatherDataList);
        assertFalse(weatherDataList.isEmpty());
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