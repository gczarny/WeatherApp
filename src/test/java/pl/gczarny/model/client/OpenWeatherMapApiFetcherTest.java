package pl.gczarny.model.client;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.gczarny.model.WeatherData;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
    public void invalidLocationShouldThrowException() throws WeatherDataFetchException {
        //given
        String location = "invalid location";
        //when
        when(mockJsonWeatherService.getJsonObjectFromApi(location)).thenThrow(new WeatherDataFetchException("Error fetching data"));
        //then
        assertThrows(WeatherDataFetchException.class, () -> openWeatherMapApiFetcher.fetchForecastData(location));
    }
    @Test
    public void fetchForecastDataShouldThrowWeatherDataFetchExceptionIfJsonObjectFromApiIsEmpty() throws Exception {
        // given
        String location = "Warsaw";
        when(mockJsonWeatherService.getJsonObjectFromApi(anyString())).thenReturn(JsonWeatherStub.getEmptyJsonFile());

        // then
        Assertions.assertThrows(WeatherDataFetchException.class, () -> {
            openWeatherMapApiFetcher.fetchForecastData(location);
        });
    }

    @Test
    public void testGetWeatherIcon() {
        // given
        var json = JsonWeatherStub.getWeatherFromJson().getAsJsonArray("list").get(0).getAsJsonObject();

        // when
        var result = openWeatherMapApiFetcher.getWeatherIcon(json);

        // then
        assertEquals("03d", result);
    }

    @Test
    public void testGetTemperature() {
        // given
        var json = JsonWeatherStub.getWeatherFromJson().getAsJsonArray("list").get(0).getAsJsonObject();

        // when
        var result = openWeatherMapApiFetcher.getTemperature(json);

        // then
        assertEquals(3.54, result, 0.001);
    }

    @Test
    public void testGetPressure() {
        // given
        var json = JsonWeatherStub.getWeatherFromJson().getAsJsonArray("list").get(0).getAsJsonObject();

        // when
        var result = openWeatherMapApiFetcher.getPressure(json);

        // then
        assertEquals(997.0, result, 0.001);
    }

    @Test
    public void testGetWindSpeed() {
        // given
        var json = JsonWeatherStub.getWeatherFromJson().getAsJsonArray("list").get(0).getAsJsonObject();

        // when
        var result = openWeatherMapApiFetcher.getWindSpeed(json);

        // then
        assertEquals(5.63, result, 0.001);
    }

    @Test
    public void testGetWindDeg() {
        // given
        var json = JsonWeatherStub.getWeatherFromJson().getAsJsonArray("list").get(0).getAsJsonObject();

        // when
        var result = openWeatherMapApiFetcher.getWindDeg(json);

        // then
        assertEquals(249.0, result, 0.001);
    }

    @Test
    public void testGetHumidity() {
        // given
        var json = JsonWeatherStub.getWeatherFromJson().getAsJsonArray("list").get(0).getAsJsonObject();

        // when
        var result = openWeatherMapApiFetcher.getHumidity(json);

        // then
        assertEquals(65.0, result, 0.001);
    }

    @Test
    public void fetchForecastDataWithEmptyResponseShouldThrowWeatherDataFetchException() throws WeatherDataFetchException {
        // given
        String location = "TestEmptyResponseLocation";
        JsonWeatherService jsonWeatherService = mock(JsonWeatherService.class);
        OpenWeatherMapApiFetcher openWeatherMapApiFetcher = new OpenWeatherMapApiFetcher(jsonWeatherService);
        when(jsonWeatherService.getJsonObjectFromApi(location)).thenReturn(JsonWeatherStub.getEmptyJsonFile());

        // then
        assertThrows(WeatherDataFetchException.class, () -> {
            openWeatherMapApiFetcher.fetchForecastData(location);
        });
    }

    @Test
    public void fetchForecastDataShouldReturnListWithFiveElements() throws WeatherDataFetchException {
        // given
        String location = "Warsaw";
        JsonWeatherService jsonWeatherService = mock(JsonWeatherService.class);
        OpenWeatherMapApiFetcher openWeatherMapApiFetcher = new OpenWeatherMapApiFetcher(jsonWeatherService);

        JsonObject jsonObjectFromApi = JsonWeatherStub.getWeatherFromJson();
        when(jsonWeatherService.getJsonObjectFromApi(location)).thenReturn(jsonObjectFromApi);

        // when
        List<WeatherData> forecastData = openWeatherMapApiFetcher.fetchForecastData(location);

        // then
        assertEquals(5, forecastData.size());
    }

}