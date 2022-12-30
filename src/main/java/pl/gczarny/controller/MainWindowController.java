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
import javafx.util.Duration;
import pl.gczarny.model.WeatherIconManager;
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
    private ImageView leftImageView;
    @FXML
    public void ackLeftLocationButton(){
        String location = leftCityTextField.getText();
        if(location.isEmpty() || location.equals("")){
            DialogUtils.warningDialog(FxmlUtils.getResourceBundle().getString("warning.left.empty.field"));
            return;
        }
        dataStatusLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.request"));

        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            WeatherData weatherData = fetchTask.getValue();
            temperatureLeftSide.setText(String.format("%.1f°C", weatherData.getTemperature()));
            System.out.println(weatherData.getDescription());
            dataStatusLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.done"));
            updateLeftImageView(weatherData.getDescription());
            resetStatusLabelAfterDelay(dataStatusLabel);
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

    void updateLeftImageView(String description){
        String iconPath = WeatherIconManager.getIconPath(description);
        Image image = new Image(iconPath);
        leftImageView.setImage(image);
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
