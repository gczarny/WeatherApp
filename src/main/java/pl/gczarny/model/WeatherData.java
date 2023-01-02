package pl.gczarny.model;

import java.time.LocalDateTime;

public class WeatherData {

    private double temperature;
    private String id;
    private String location;
    LocalDateTime localDateTime;


    public WeatherData(double temperature, String id){
        this.temperature = temperature;
        this.id = id;
    }

    public WeatherData(double temperature, String location, String id, LocalDateTime dateTime) {
        this.temperature = temperature;
        this.id = id;
        this.location = location;
        this.localDateTime = dateTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDateTime() {
        return localDateTime;
    }
}
