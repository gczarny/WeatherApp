package pl.gczarny.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.gczarny.model.WeatherDataFetcher;

public class MainWindowController {

    @FXML
    private TextField leftCityTextField;
    @FXML
    private Label temperatureLeftSide;
    @FXML
    public void ackLeftLocationButton(){
        System.out.println("Button clicked!");
        // Pobierz miasto z pola tekstowego
        String location = leftCityTextField.getText();
        double temperature = WeatherDataFetcher.getTemperature(location);
        temperatureLeftSide.setText(String.format("%.1f°C", temperature));
    }
}
