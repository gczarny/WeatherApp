package pl.gczarny.view;

public class WeatherIconManager {
    private static final String ICON_URL_PATTERN = "http://openweathermap.org/img/wn/%s@2x.png";
    private static final String ICON_EMPTY_PATH = "/images/empty_icon.png";
    private static String getIconUrl(String icon) {
        return String.format(ICON_URL_PATTERN, icon);
    }
    public static String getIconPath(String icon) {
        return getIconUrl(icon);
    }

    public static String getEmptyIcon(){
        return ICON_EMPTY_PATH;
    }
}
