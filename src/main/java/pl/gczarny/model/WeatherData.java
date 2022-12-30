package pl.gczarny.model;

public class WeatherData {

    private double temperature;
    private String description, id;

    public WeatherData(double temperature) {
        this.temperature = temperature;
    }

    public WeatherData(double temperature, String description){
        this.temperature = temperature;
        this.description = description;
    }

    public WeatherData(double temperature, String description, String id){
        this.temperature = temperature;
        this.description = description;
        this.id = id;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
}
