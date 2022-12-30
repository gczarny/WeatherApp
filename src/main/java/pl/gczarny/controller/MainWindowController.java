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
import javafx.util.Duration;
import pl.gczarny.utils.DialogUtils;
import pl.gczarny.model.WeatherData;
import pl.gczarny.model.WeatherDataFetchTask;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

public class MainWindowController {

    private String status;
    @FXML
    private TextField leftCityTextField;
    @FXML
    private Label temperatureLeftSide;
    @FXML
    private Label dataStatusLabel;
    @FXML
    private Label errorLabel;
    @FXML
    public void ackLeftLocationButton(){
        String location = leftCityTextField.getText();
        if(location.isEmpty() || location.equals("")){
            errorLabel.setText("Pole nie może być puste");
            return;
        }
        errorLabel.setText("");
        dataStatusLabel.setText("Pobieranie danych z serwera...");

        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            WeatherData weatherData = fetchTask.getValue();
            if(Double.isNaN(weatherData.getTemperature())){
                /*dataStatusLabel.setText("Nie odnaleziono miasta");
                DialogUtils.errorDialog(FxmlUtils.getResourceBundle().getString("error.not.found"));*/
            }else{
                temperatureLeftSide.setText(String.format("%.1f°C", weatherData.getTemperature()));
                dataStatusLabel.setText("Dane zostały pobrane pomyślnie");
                resetStatusLabelAfterDelay(dataStatusLabel);
            }
        });
        fetchTask.setOnFailed(event -> {
            Throwable exception = fetchTask.getException();
            if(exception instanceof WeatherDataFetchException){
                DialogUtils.errorDialog(exception.getMessage());
                dataStatusLabel.setText("");
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
