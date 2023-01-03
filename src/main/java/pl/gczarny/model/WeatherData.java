package pl.gczarny.model;

import java.time.LocalDateTime;

public class WeatherData {

    private double temperature;
    private double pressure;
    private double windSpeed, windDeg;
    private double humidity;
    private int population;
    private String id;
    private String location;
    LocalDateTime localDateTime;

    public WeatherData(double temperature, double pressure, double windSpeed,
                       double windDeg, double humidity, String id, String location,
                       LocalDateTime localDateTime, int population) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windDeg = windDeg;
        this.humidity = humidity;
        this.id = id;
        this.location = location;
        this.localDateTime = localDateTime;
        this.population = population;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return localDateTime;
    }

    public double getPressure() {
        return pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDeg() {
        return windDeg;
    }

    public double getHumidity() {
        return humidity;
    }
    public int getPopulation() {
        return population;
    }
}
