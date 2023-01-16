package pl.gczarny.view;

public class WeatherIconManager {
    private static final String ICON_URL_PATTERN = "http://openweathermap.org/img/wn/%s@2x.png";
    private static String getIconUrl(String icon) {
        return String.format(ICON_URL_PATTERN, icon);
    }
    public static String getIconPath(String icon) {
        return getIconUrl(icon);
    }
}
