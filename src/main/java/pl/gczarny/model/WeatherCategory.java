package pl.gczarny.model;

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

    public String getDescription() {
        return description;
    }
}
