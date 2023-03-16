package pl.gczarny.model.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JsonWeatherServiceTest {
    private JsonWeatherService jsonWeatherService;
    private String location;
    @BeforeEach
    public void setUp() {
        jsonWeatherService = mock(JsonWeatherService.class);
        location = "Warsaw";
    }

    @Test
    public void receivedJsonObjectShouldHaveCodEqualTo200() throws Exception {
        // given
        JsonObject jsonObjectFromApi = JsonWeatherStub.getWeatherFromJson();
        // when
        when(jsonWeatherService.getJsonObjectFromApi(location)).thenReturn(jsonObjectFromApi);

        // then
        assertEquals(200, jsonObjectFromApi.get("cod").getAsInt());
    }

    @Test
    public void receivedJsonObjectFromApiShouldNotBeNull() throws Exception {
        // given
        JsonObject jsonObjectFromApi = JsonWeatherStub.getWeatherFromJson();
        // when
        when(jsonWeatherService.getJsonObjectFromApi(location)).thenReturn(jsonObjectFromApi);
        // then
        assertNotNull(jsonObjectFromApi);
        assertNotNull(jsonObjectFromApi.getAsJsonArray("list"));
    }

    @Test
    public void wrongLocationShouldThrowWeatherDataFetchException() {
        // given
        String location = "NonExistingCity";

        //when
        try {
            doThrow(new WeatherDataFetchException("Location not found")).when(jsonWeatherService).getJsonObjectFromApi(location);
        } catch (WeatherDataFetchException e) {
            throw new RuntimeException(e);
        }

        // then
        assertThrows(WeatherDataFetchException.class, () -> {
            jsonWeatherService.getJsonObjectFromApi(location);
        });
    }

    @Test
    void validateEmptyJsonDataShouldThrowExceptionWithResponseEmptyMessage() {
        //given
        JsonWeatherService jsonWeatherService = new JsonWeatherService();

        //when
        WeatherDataFetchException exception = assertThrows(WeatherDataFetchException.class, () -> jsonWeatherService.validateJsonData(""));

        //then
        assertEquals(FxmlUtils.getResourceBundle().getString("error.response.empty"), exception.getMessage());
    }

}