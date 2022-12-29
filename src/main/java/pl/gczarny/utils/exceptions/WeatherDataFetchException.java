package pl.gczarny.utils.exceptions;

public class WeatherDataFetchException extends Exception{
    private String message;

    public WeatherDataFetchException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
