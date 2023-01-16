package pl.gczarny.view;

import com.google.common.collect.Range;
import javafx.scene.layout.*;
import pl.gczarny.utils.DialogUtils;
import pl.gczarny.utils.FxmlUtils;

import java.net.URL;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherBackgroundManager {
    private static final String PATH_PATTERN = "/images/backgrounds/";
    private static final BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);

    private static final Map<Range<Integer>, WeatherCategory> WEATHER_DESCRIPTION_BY_ID_RANGE = Map.of(
            Range.closed(200, 232), WeatherCategory.THUNDERSTORM,
            Range.closed(300, 531), WeatherCategory.RAIN,
            Range.closed(600, 622), WeatherCategory.SNOW,
            Range.closed(701, 781), WeatherCategory.ATMOSPHERE,
            Range.closed(800, 800), WeatherCategory.CLEAR,
            Range.closed(801, 804), WeatherCategory.CLOUDS
    );

    private static final Map<WeatherCategory, Map<WeatherTimeOfDay, LazyImage>> BACKGROUND_IMAGE_BY_WEATHER_DESCRIPTION_BY_TIME_OF_DAY = new HashMap<>();

    static {
        for (WeatherCategory category : WeatherCategory.values()) {
            Map<WeatherTimeOfDay, LazyImage> imagesByTimeOfDay = new EnumMap<>(WeatherTimeOfDay.class);
            for (WeatherTimeOfDay timeOfDay : WeatherTimeOfDay.values()) {
                String fileName = category.name().toLowerCase() + "_" + timeOfDay.name().toLowerCase() + ".png";
                URL fileUrl = WeatherBackgroundManager.class.getResource(PATH_PATTERN + fileName);
                if (fileUrl == null) {
                    throw new IllegalStateException("File not found :" + PATH_PATTERN + fileName);
                }
                imagesByTimeOfDay.put(timeOfDay, new LazyImage(fileUrl.toString()));
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

        WeatherTimeOfDay timeOfDay = getTimeOfDay(localTime);
        LazyImage backgroundImage = BACKGROUND_IMAGE_BY_WEATHER_DESCRIPTION_BY_TIME_OF_DAY.get(weatherDescription).get(timeOfDay);
        try{
            BackgroundImage bgImage = new BackgroundImage(backgroundImage.getImageView().getImage(), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
            pane.setBackground(new Background(bgImage));
        } catch (NullPointerException e){
            DialogUtils.errorDialog(e.getMessage());
        } catch (IllegalArgumentException e){
            DialogUtils.errorDialog(e.getMessage());
        }
    }

    private static WeatherTimeOfDay getTimeOfDay(LocalTime localTime){
        if (localTime.isBefore(LocalTime.of(12, 0))) {
            return WeatherTimeOfDay.MORNING;
        } else if (localTime.isBefore(LocalTime.of(18, 0))) {
            //return "Afternoon";
            return WeatherTimeOfDay.AFTERNOON;
        } else if (localTime.isBefore(LocalTime.of(20, 0))) {
            //return "Evening";
            return WeatherTimeOfDay.EVENING;
        } else {
            //return "Night";
            return WeatherTimeOfDay.NIGHT;
        }
    }
}
