package pl.gczarny.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import pl.gczarny.controller.tasks.WeatherDataFetchTask;
import pl.gczarny.model.WeatherData;
import pl.gczarny.utils.DialogUtils;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;
import pl.gczarny.view.WeatherBackgroundManager;
import pl.gczarny.view.WeatherIconManager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class NestedController implements Initializable {

    @FXML
    private Label statusLabel;
    @FXML
    private Label city;
    @FXML
    private Label population;
    @FXML
    private Label vBoxInstanceDate;
    @FXML
    private Label vBoxInstanceHumidity;
    @FXML
    private Label vBoxInstancePressure;
    @FXML
    private Label vBoxInstanceWindMax;
    @FXML
    private Label vBoxInstanceWindSpeed;
    @FXML
    private HBox hBoxForecast;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void updateStatusLabel(String text){
        statusLabel.setText(text);
    }
    public void fetchForecast(String location, boolean onButtonDemand, boolean leftSide, Pane pane){
        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            List<WeatherData> weatherDataList = fetchTask.getValue();
            hBoxForecast = displayForecastInHBox(weatherDataList);
            setPopulationLabel(weatherDataList.get(0).getPopulation());
            city.setText(location);
            WeatherBackgroundManager.changeBackground(weatherDataList.get(0).getId(), pane, LocalTime.now());
            if(onButtonDemand){
                btnDemandActionOnSucceededEvent(weatherDataList.get(0).getLocation());
                if(leftSide){
                    FxmlUtils.setLeftLocation(weatherDataList.get(0).getLocation());
                }else{
                    FxmlUtils.setRightLocation(weatherDataList.get(0).getLocation());
                }
                FxmlUtils.writeConfigFile();
            }
        });
        fetchTask.setOnFailed(event -> {
            setOnFailedEvent(fetchTask);
        });
        new Thread(fetchTask).start();
    }

    private void setPopulationLabel(int population){
        this.population.setText(Integer.toString(population));
        if(population >= 1000000){
            this.population.setText(">" + population);
        }
    }
    private void btnDemandActionOnSucceededEvent(String location){
        resetStatusLabelAfterDelay(statusLabel, 3);
        city.setText(location);
        statusLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.done"));
    }

    private void setOnFailedEvent(WeatherDataFetchTask task){
        Throwable exception = task.getException();
        if(exception instanceof WeatherDataFetchException){
            DialogUtils.errorDialog(exception.getMessage());
            statusLabel.setText("");
        }else {
            exception.printStackTrace();
            DialogUtils.errorDialog(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        }
    }

    private ImageView updateImageViews(String id){
        ImageView weatherIcon = new ImageView();
        Image icon = new Image(WeatherIconManager.getIconPath(id));
        weatherIcon.setImage(icon);
        weatherIcon.setFitHeight(Region.USE_COMPUTED_SIZE);
        weatherIcon.setFitWidth(Region.USE_COMPUTED_SIZE);
        return weatherIcon;
    }
    private void makeVBoxClickable(VBox vBox, WeatherData weatherData) {
        vBox.setOnMouseClicked(event -> {
            setInnerShadowEffectOnVBoxAndSetData(vBox, weatherData);
            for (Node node : hBoxForecast.getChildren()) {
                if (node instanceof VBox && node != vBox) {
                    ((VBox) node).setEffect(null);
                }
            }
        });
    }
    private HBox displayForecastInHBox(List<WeatherData> forecastList) {
        hBoxForecast.getChildren().clear();
        BackgroundFill backgroundFill = new BackgroundFill(new Color(0, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        hBoxForecast.setBackground(background);
        for (WeatherData forecastData : forecastList) {
            VBox vBox = getVBox(forecastList, new Insets(10, 10, 10, 10));
            Label temperatureLabel = createLabelWithPrefSize(String.format("%.1f°C", forecastData.getTemperature()), true,
                    150, 150, 12, "Verdana");
            Label dateLabel = createLabel(String.format(forecastData.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE)), 12, "Verdana");
            vBox.getChildren().addAll(temperatureLabel, dateLabel, new Separator(), updateImageViews(forecastData.getIcon()));
            vBox.getProperties().put("weatherData", forecastData);
            makeVBoxClickable(vBox, forecastData);
            vBox.setCursor(Cursor.HAND);
            hBoxForecast.getChildren().addAll(vBox, addVerticalSeparator());
            HBox.setHgrow(vBox, Priority.ALWAYS);
        }
        setInnerShadowForFirstVBox(forecastList.get(0));
        return hBoxForecast;
    }
    private void setInnerShadowForFirstVBox(WeatherData weatherData) {
        if (hBoxForecast.getChildren().size() > 0) {
            Node firstChild = hBoxForecast.getChildren().get(0);
            if (firstChild instanceof VBox) {
                VBox firstVBox = (VBox) firstChild;
                setInnerShadowEffectOnVBoxAndSetData(firstVBox, weatherData);
            }
        }
    }
    private void setInnerShadowEffectOnVBoxAndSetData(VBox vbox, WeatherData weatherData){
        vbox.setEffect(new InnerShadow(10, Color.RED));
        setDataInHBoxTakenFromClickedVBox(weatherData);
    }
    private void setDataInHBoxTakenFromClickedVBox(WeatherData weatherData){
        vBoxInstanceDate.setText(weatherData.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        vBoxInstanceDate.setWrapText(true);
        vBoxInstancePressure.setText(weatherData.getPressure() + " hPa");
        vBoxInstanceHumidity.setText(weatherData.getHumidity() + "%");
        vBoxInstanceWindSpeed.setText(weatherData.getWindSpeed() + " m/s");
        vBoxInstanceWindMax.setText(weatherData.getWindDeg() + "°");
    }

    private Separator addVerticalSeparator(){
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        return separator;
    }

    private VBox getVBox(List<WeatherData> forecastList, Insets insets){
        VBox vBox = new VBox();
        vBox.setPrefWidth(hBoxForecast.getWidth() / forecastList.size());
        vBox.setSpacing(10);
        vBox.setPadding(insets);
        vBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }
    private void resetStatusLabelAfterDelay(Label label, double delay) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(delay), event -> label.setText("")));
        timeline.play();
    }
    private Label createLabel(String text, int fontSize, String fontFamily){
        Label label = new Label(text);
        label.setTextAlignment(TextAlignment.JUSTIFY);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font(fontFamily, FontWeight.BOLD,fontSize));
        label.setWrapText(true);
        return label;
    }
    private Label createLabelWithPrefSize(String text, boolean setPrefSize, double prefWidth, double prefHeight, int fontSize, String fontFamily){
        Label label = new Label(text);
        if(setPrefSize){
            label.setPrefSize(prefWidth, prefHeight);
        }
        label.setTextAlignment(TextAlignment.JUSTIFY);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font(fontFamily, FontWeight.BOLD,fontSize));
        label.setWrapText(true);
        return label;
    }
}
