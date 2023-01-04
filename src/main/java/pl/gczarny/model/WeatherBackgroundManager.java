package pl.gczarny.model;

import com.google.common.collect.Range;
import javafx.animation.*;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.*;
import javafx.util.Duration;
import pl.gczarny.utils.DialogUtils;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherBackgroundManager {
    private static final String PATH_PATTERN = "/images/backgrounds/";

    private static final Map<Range<Integer>, WeatherCategory> WEATHER_DESCRIPTION_BY_ID_RANGE = Map.of(
            Range.closed(200, 232), WeatherCategory.THUNDERSTORM,
            Range.closed(300, 531), WeatherCategory.RAIN,
            Range.closed(600, 622), WeatherCategory.SNOW,
            Range.closed(701, 781), WeatherCategory.ATMOSPHERE,
            Range.closed(800, 800), WeatherCategory.CLEAR,
            Range.closed(801, 804), WeatherCategory.CLOUDS
    );

    private static final Map<WeatherCategory, Map<String, LazyImage>> BACKGROUND_IMAGE_BY_WEATHER_DESCRIPTION_BY_TIME_OF_DAY = new HashMap<>();

    static {
        for (WeatherCategory category : WeatherCategory.values()) {
            Map<String, LazyImage> imagesByTimeOfDay = new HashMap<>();
            for (String timeOfDay : List.of("Morning", "Afternoon", "Evening", "Night")) {
                String path = PATH_PATTERN + category.name().toLowerCase() + "_" + timeOfDay.toLowerCase() + ".png";
                imagesByTimeOfDay.put(timeOfDay, new LazyImage(path));
            }
            BACKGROUND_IMAGE_BY_WEATHER_DESCRIPTION_BY_TIME_OF_DAY.put(category, imagesByTimeOfDay);
        }
    }

    public static void changeBackground(int id, Pane pane, LocalTime localTime){
        WeatherCategory weatherDescription = WEATHER_DESCRIPTION_BY_ID_RANGE.entrySet().stream()
                .filter(entry -> entry.getKey().contains(id))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();

        String timeOfDay = getTimeOfDay(localTime);
        LazyImage backgroundImage = BACKGROUND_IMAGE_BY_WEATHER_DESCRIPTION_BY_TIME_OF_DAY.get(weatherDescription).get(timeOfDay);
        try{
            BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);
            BackgroundImage bgImage = new BackgroundImage(backgroundImage.getImageView().getImage(), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
            pane.setBackground(new Background(bgImage));
        } catch (NullPointerException e){
                DialogUtils.errorDialog("Nie mogłem wczytać tła!");
        }
    }

    private static String getTimeOfDay(LocalTime localTime){
        if (localTime.isBefore(LocalTime.of(12, 0))) {
            return "Morning";
        } else if (localTime.isBefore(LocalTime.of(18, 0))) {
            return "Afternoon";
        } else if (localTime.isBefore(LocalTime.of(20, 0))) {
            return "Evening";
        } else {
            return "Night";
        }
    }
}
/* -fx-background-image url('/images/left_bg.png')
-fx-background-size cover
-fx-background-position center
*/
