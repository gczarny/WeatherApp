package pl.gczarny.model;

public class WeatherIconManager {
    private static final String ICON_URL_PATTERN = "http://openweathermap.org/img/wn/%s@2x.png";
    private static String getIconUrl(String id) {
        return String.format(ICON_URL_PATTERN, id);
    }

    public static String getIconPath(String id) {
        return getIconUrl(id);
    }
}
