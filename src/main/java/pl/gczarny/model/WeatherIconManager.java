package pl.gczarny.model;

import javafx.scene.image.Image;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WeatherIconManager {
    /*private static final Map<String, String> ICONS_BY_DESCRIPTION;
    static {
        Map<String, String> iconsByDescription = new HashMap<>();
        iconsByDescription.put("clear sky", "/icons/sunny.png");
        iconsByDescription.put("few clouds", "/icons/partly_cloudy.png");
        iconsByDescription.put("scattered clouds", "/icons/scattered_clouds.png");
        iconsByDescription.put("broken clouds", "/icons/partly_cloudy.png");
        iconsByDescription.put("shower rain", "/icons/rainy.png");
        iconsByDescription.put("rain", "/icons/rainy.png");
        iconsByDescription.put("thunderstorm", "/icons/stormy.png");
        iconsByDescription.put("snow", "/icons/stormy.png");
        iconsByDescription.put("mist", "/icons/stormy.png");
        ICONS_BY_DESCRIPTION = Collections.unmodifiableMap(iconsByDescription);
    }
    public static String getIconPath(String description) {
        String iconPath = ICONS_BY_DESCRIPTION.get(description);
        if (iconPath == null) {
            return "/icons/na.png";
        }
        return iconPath;
    }*/
    private static final String ICON_URL_PATTERN = "http://openweathermap.org/img/wn/%s@2x.png";
    private static String getIconUrl(String id) {
        return String.format(ICON_URL_PATTERN, id);
    }

    public static String getIconPath(String id) {
        return getIconUrl(id);
    }



/*    private static final String SUNNY_ICON_URL = "/icons/sunny.png";
    private static final String CLOUDY_ICON_URL = "/icons/cloudy.png";
    private static final String RAINY_ICON_URL = "/icons/rainy.png";
    private static final String PARTLY_CLOUDY_ICON_URL = "/icons/partly_cloudy.png";
    private static final String STORMY_ICON_URL = "/icons/stormy.png";

    public static Image getWeatherIcon(String weatherDescription) {
        if (weatherDescription.contains("sunny")) {
            return new Image(SUNNY_ICON_URL);
        } else if (weatherDescription.contains("cloudy")) {
            return new Image(CLOUDY_ICON_URL);
        } else if (weatherDescription.contains("rain")) {
            return new Image(RAINY_ICON_URL);
        } else if (weatherDescription.contains("snow")) {
            return new Image(STORMY_ICON_URL);
        } else {
            // w przypadku braku pasującego opisu pogody zwróć obrazek z chmurami
            return new Image(CLOUDY_ICON_URL);
        }
    }*/
}
