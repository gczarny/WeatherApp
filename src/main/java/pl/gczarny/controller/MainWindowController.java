package pl.gczarny.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import pl.gczarny.model.*;
import pl.gczarny.utils.DialogUtils;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class MainWindowController {

    private String status;
    @FXML
    private TextField leftCityTextField;
    @FXML
    private Label statusLeftLabel;
    @FXML
    private HBox HBoxForecast;
    @FXML
    public void ackLeftLocationButton(){
        String location = leftCityTextField.getText();
        if(location.isEmpty() || location.equals("")){
            DialogUtils.warningDialog(FxmlUtils.getResourceBundle().getString("warning.left.empty.field"));
            return;
        }
        statusLeftLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.request"));

        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            List<WeatherData> weatherDataList = fetchTask.getValue();
            displayForecast(weatherDataList);
            statusLeftLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.done"));
            resetStatusLabelAfterDelay(statusLeftLabel);
        });
        fetchTask.setOnFailed(event -> {
            Throwable exception = fetchTask.getException();
            if(exception instanceof WeatherDataFetchException){
                DialogUtils.errorDialog(exception.getMessage());
                statusLeftLabel.setText("");
            }else
                DialogUtils.errorDialog(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        });
        new Thread(fetchTask).start();
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

    private ImageView updateLeftImageViews(String id){
        ImageView weatherIcon = new ImageView();
        Image icon = new Image(WeatherIconManager.getIconPath(id));
        weatherIcon.setImage(icon);
        weatherIcon.setFitHeight(55);
        weatherIcon.setFitWidth(55);
        return weatherIcon;
    }

    private void displayForecast(List<WeatherData> forecastList) {
        HBox forecastBox = new HBox();
        forecastBox.setSpacing(30);
        for (WeatherData forecastData : forecastList) {
            VBox vBox = new VBox();
            vBox.setSpacing(10);
            Label temperatureLabel = new Label(String.format("%.1f°C", forecastData.getTemperature()));
            Label dateLabel = new Label(forecastData.getDateTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
            dateLabel.setWrapText(true);
            //Label locationLabel = new Label(forecastData.getLocation());
            vBox.getChildren().addAll(temperatureLabel, dateLabel, updateLeftImageViews(forecastData.getId()));
            forecastBox.getChildren().add(vBox);
        }
        HBoxForecast.getChildren().add(forecastBox);
    }

    private void resetStatusLabelAfterDelay(Label label) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(3000),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        label.setText("");
                    }
                }
        ));
        timeline.play();
    }

}
