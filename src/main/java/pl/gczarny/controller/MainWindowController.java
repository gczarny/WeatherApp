package pl.gczarny.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import pl.gczarny.model.*;
import pl.gczarny.utils.DialogUtils;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MainWindowController{
    @FXML
    private TextField leftCityTextField;
    @FXML
    private Label statusLeftLabel;
    @FXML
    private Label city;
    @FXML
    private HBox HBoxForecast;
    private String location;
    @FXML
    public void ackLeftLocationButton(){
        if(leftCityTextField.getText().equals(location)){
            return;
        }
        location = leftCityTextField.getText();

        if(location.isEmpty() || location.equals("")){
            DialogUtils.warningDialog(FxmlUtils.getResourceBundle().getString("warning.left.empty.field"));
            return;
        }
        statusLeftLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.request"));
        city.setText(location);
        fetchForecastData(location, true);
    }
    @FXML
    void closeApplication() {
        if(DialogUtils.confirmCloseApplication().get() == ButtonType.OK){
            Platform.exit();
            System.exit(0);
        }
    }
    @FXML
    void setCaspian() {
        Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
    }

    @FXML
    void setModena() {
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
    }

    @FXML
    void showAbout() {
        DialogUtils.dialogAboutApplication();
    }

    private void fetchForecastData(String location, boolean onButtonDemand){
        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            List<WeatherData> weatherDataList = fetchTask.getValue();
            displayForecastInHBox(weatherDataList);
            if(onButtonDemand){
                statusLeftLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.done"));
                resetStatusLabelAfterDelay(statusLeftLabel);
                FxmlUtils.setConfigResourceProperty("location", location);
            }
        });
        fetchTask.setOnFailed(event -> {
            Throwable exception = fetchTask.getException();
            if(exception instanceof WeatherDataFetchException){
                DialogUtils.errorDialog(exception.getMessage());
                statusLeftLabel.setText("");
            }else {
                DialogUtils.errorDialog(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
            }
        });
        new Thread(fetchTask).start();
    }

    private ImageView updateLeftImageViews(String id){
        ImageView weatherIcon = new ImageView();
        Image icon = new Image(WeatherIconManager.getIconPath(id));
        weatherIcon.setImage(icon);
        return weatherIcon;
    }

    private void displayForecastInHBox(List<WeatherData> forecastList) {
        HBoxForecast.getChildren().clear();
        //HBoxForecast.setSpacing(30);
        BackgroundFill backgroundFill = new BackgroundFill(new Color(0, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        HBoxForecast.setBackground(background);
        for (WeatherData forecastData : forecastList) {
            VBox vBox = new VBox();
            vBox.setPrefWidth(HBoxForecast.getWidth() / forecastList.size());
            vBox.setSpacing(10);
            vBox.setPadding(new Insets(10, 10, 10, 10));
            vBox.setAlignment(Pos.CENTER);
            Label temperatureLabel = new Label(String.format("%.1f°C", forecastData.getTemperature()));
            Label dateLabel = new Label(forecastData.getDateTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
            dateLabel.setTextAlignment(TextAlignment.JUSTIFY);
            dateLabel.setWrapText(true);
            vBox.getChildren().addAll(temperatureLabel, dateLabel, new Separator(), updateLeftImageViews(forecastData.getId()));
            HBoxForecast.getChildren().addAll(vBox, addVerticalSeparator());
            HBox.setHgrow(vBox, Priority.ALWAYS);
        }
    }

    private Separator addVerticalSeparator(){
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        //separator.setStyle("-fx-border-color: blue;");
        return separator;
    }

    private void resetStatusLabelAfterDelay(Label label) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(3000),
                event -> label.setText("")
        ));
        timeline.play();
    }

    @FXML
    public void initialize() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/config");
            location = bundle.getString("location");
            leftCityTextField.setText(location);
            city.setText(location);
            fetchForecastData(location, false);
        } catch (MissingResourceException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
    }
}
