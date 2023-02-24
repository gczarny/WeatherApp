package pl.gczarny.model.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import com.google.gson.JsonObject;
import pl.gczarny.model.WeatherData;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import static org.junit.jupiter.api.Assertions.*;

class JsonWeatherServiceTest {
    private JsonWeatherService jsonWeatherService;

    @BeforeEach
    public void setUp() {
        jsonWeatherService = new JsonWeatherService();
    }

    @Test
    public void receivedJsonObjectShouldHaveCodEqualTo200() throws Exception {
        // given
        String location = "Warsaw";

        // when
        JsonObject jsonObjectFromApi = jsonWeatherService.getJsonObjectFromApi(location);
        // then
        assertEquals(200, jsonObjectFromApi.get("cod").getAsInt());
    }

    @Test
    public void receivedJsonObjectFromApiShouldNotBeNull() throws Exception {
        // given
        String location = "Warsaw";

        // when
        JsonObject jsonObjectFromApi = jsonWeatherService.getJsonObjectFromApi(location);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(jsonObjectFromApi));
        // then
        assertNotNull(jsonObjectFromApi);
        assertNotNull(jsonObjectFromApi.getAsJsonArray("list"));
    }

    @Test
    public void wrongLocationShouldThrowWeatherDataFetchException() {
        // given
        String location = "NonExistingCity";

        // then
        assertThrows(WeatherDataFetchException.class, () -> {
            jsonWeatherService.getJsonObjectFromApi(location);
        });
    }

    @Test
    public void fetchForecastDataShouldReturnListWithFiveElements() throws WeatherDataFetchException {
        // given
        String location = "Warsaw";
        WeatherClient client = new OpenWeatherMapApiFetcher(new JsonWeatherService());

        // when
        List<WeatherData> forecastData = client.fetchForecastData(location);

        // then
        assertEquals(5, forecastData.size());
    }
}