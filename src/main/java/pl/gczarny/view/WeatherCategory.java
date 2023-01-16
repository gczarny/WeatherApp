package pl.gczarny.view;

public enum WeatherCategory {
    THUNDERSTORM("Thunderstorm"),
    RAIN("Rain"),
    SNOW("Snow"),
    ATMOSPHERE("Atmosphere"),
    CLEAR("Clear"),
    CLOUDS("Clouds");

    private final String description;

    WeatherCategory(String description) {
        this.description = description;
    }

}
