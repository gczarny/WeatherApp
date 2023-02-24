package pl.gczarny.model.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.gczarny.model.WeatherData;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenWeatherMapApiFetcherTest {

    private OpenWeatherMapApiFetcher openWeatherMapApiFetcher;
    private JsonWeatherService mockJsonWeatherService;

    @BeforeEach
    public void setUp() {
        mockJsonWeatherService = mock(JsonWeatherService.class);
        openWeatherMapApiFetcher = new OpenWeatherMapApiFetcher(mockJsonWeatherService);
    }
    @Test
    public void weatherIconShouldReturnCorrectIconValue() {
        //given
        JsonObject json = new JsonObject();
        JsonArray weatherArray = new JsonArray();
        JsonObject weather = new JsonObject();
        //when
        weather.addProperty("icon", "10d");
        weatherArray.add(weather);
        json.add("weather", weatherArray);
        //then
        assertEquals("10d", openWeatherMapApiFetcher.getWeatherIcon(json));
    }
    @Test
    public void invalidLocationShouldThrowException() throws WeatherDataFetchException {
        //given
        String location = "invalid location";
        when(mockJsonWeatherService.getJsonObjectFromApi(location)).thenThrow(new WeatherDataFetchException("Error fetching data"));
        //when
        assertThrows(WeatherDataFetchException.class, () -> openWeatherMapApiFetcher.fetchForecastData(location));
    }

    @Test
    public void s() throws WeatherDataFetchException {
        //given
        String location = "Warsaw";
        when(mockJsonWeatherService.getJsonObjectFromApi(location)).thenThrow(new WeatherDataFetchException("Error fetching data"));
        //when
        assertThrows(WeatherDataFetchException.class, () -> openWeatherMapApiFetcher.fetchForecastData(location));
    }

    @Test
    public void testFetchForecastDataReturnsFiveElements() throws Exception {
        // given
        String location = "Warsaw";
        LocalDateTime localDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 24, 12, 0);
        when(mockJsonWeatherService.getJsonObjectFromApi(location)).thenReturn(OpenWeatherMapApiFetcherStub.getJsonObjectFromApi());

        // when
        List<WeatherData> weatherDataList = openWeatherMapApiFetcher.fetchForecastData(location);

        // then
        assertEquals(5, weatherDataList.size());
        assertEquals(1.5, weatherDataList.get(0).getTemperature());
        assertEquals(1025.5, weatherDataList.get(0).getPressure());
        assertEquals(3.5, weatherDataList.get(0).getWindSpeed());
        assertEquals(220.5, weatherDataList.get(0).getWindDeg());
        assertEquals(60.5, weatherDataList.get(0).getHumidity());
        assertEquals("Warsaw", weatherDataList.get(0).getLocation());
        assertEquals(1702132, weatherDataList.get(0).getPopulation());
    }
}