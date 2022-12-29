package pl.gczarny.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.gczarny.model.WeatherData;
import pl.gczarny.model.WeatherDataFetchTask;
import pl.gczarny.model.WeatherDataFetcher;

public class MainWindowController {

    private String status;
    @FXML
    private TextField leftCityTextField;
    @FXML
    private Label temperatureLeftSide;
    @FXML
    private Label dataStatusLabel;
    @FXML
    public void ackLeftLocationButton(){

        String location = leftCityTextField.getText();
        dataStatusLabel.setText("Pobieranie danych z serwera...");

        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            WeatherData weatherData = fetchTask.getValue();
            temperatureLeftSide.setText(String.format("%.1f°C", weatherData.getTemperature()));
            dataStatusLabel.setText("Dane zostały pobrane pomyślnie");
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                // przywrócenie poprzedniej wartości pola status
                dataStatusLabel.setText("");
            }).start();
        });
        fetchTask.setOnFailed(event -> {
            dataStatusLabel.setText("Wystąpił błąd podczas pobierania danych.");
        });
        new Thread(fetchTask).start();


        /*double temperature = WeatherDataFetcher.getTemperature(location);
        temperatureLeftSide.setText(String.format("%.1f°C", temperature));*/
    }

}
