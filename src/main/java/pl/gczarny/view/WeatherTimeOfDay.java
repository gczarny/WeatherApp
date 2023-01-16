package pl.gczarny.view;

public enum WeatherTimeOfDay {
    MORNING("morning"),
    AFTERNOON("afternoon"),
    EVENING("evening"),
    NIGHT("night");

    private final String timeOfDay;

    WeatherTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }
}
