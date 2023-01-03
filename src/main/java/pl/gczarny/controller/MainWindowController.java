package pl.gczarny.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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
    private Label population;
    @FXML
    private Label vboxInstanceData;

    @FXML
    private Label vboxInstanceHumidity;

    @FXML
    private Label vboxInstancePressure;

    @FXML
    private Label vboxInstanceWindMax;

    @FXML
    private Label vboxInstanceWindSpeed;
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
    void refreshButton() {
        if(leftCityTextField.getText().isEmpty() || location.isEmpty()){
            return;
        }
        leftCityTextField.setText(location);
        fetchForecastData(location, true);
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
        leftCityTextFieldEnterKeyListener();
    }

    private void fetchForecastData(String location, boolean onButtonDemand){
        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            List<WeatherData> weatherDataList = fetchTask.getValue();
            displayForecastInHBox(weatherDataList);
            setInnerShadowForFirstVBox(weatherDataList.get(0));
            population.setText(Integer.toString(weatherDataList.get(0).getPopulation()));
            if(weatherDataList.get(0).getPopulation() >= 1000000){
                population.setText(">" + Integer.toString(weatherDataList.get(0).getPopulation()));
            }
            if(onButtonDemand){
                statusLeftLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.done"));
                resetStatusLabelAfterDelay(statusLeftLabel);
                city.setText(location);
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
        weatherIcon.setFitHeight(Region.USE_COMPUTED_SIZE);
        weatherIcon.setFitWidth(Region.USE_COMPUTED_SIZE);
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
            vBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            vBox.setAlignment(Pos.CENTER);
            Label temperatureLabel = new Label(String.format("%.1f°C", forecastData.getTemperature()));
            temperatureLabel.setPrefSize(150, 150);
            Label dateLabel = new Label(forecastData.getDateTime().format(DateTimeFormatter.ISO_DATE));
            dateLabel.setTextAlignment(TextAlignment.JUSTIFY);
            dateLabel.setAlignment(Pos.CENTER);
            dateLabel.setWrapText(true);
            vBox.getChildren().addAll(temperatureLabel, dateLabel, new Separator(), updateLeftImageViews(forecastData.getId()));
            vBox.getProperties().put("weatherData", forecastData);
            makeVBoxClickable(vBox, forecastData);
            vBox.setCursor(Cursor.HAND);
            HBoxForecast.getChildren().addAll(vBox, addVerticalSeparator());
            HBox.setHgrow(vBox, Priority.ALWAYS);
        }
    }

    private void makeVBoxClickable(VBox vBox, WeatherData weatherData) {
        vBox.setOnMouseClicked(event -> {
            vBox.setEffect(new InnerShadow(10, Color.RED));
            setDataInHboxDataOfVBoxClickedElement(weatherData);
            // Delete effects from other VBox
            for (Node node : HBoxForecast.getChildren()) {
                if (node instanceof VBox && node != vBox) {
                    ((VBox) node).setEffect(null);
                }
            }
        });
    }

    private void setInnerShadowForFirstVBox(WeatherData weatherData) {
        if (HBoxForecast.getChildren().size() > 0) {
            Node firstChild = HBoxForecast.getChildren().get(0);
            if (firstChild instanceof VBox) {
                VBox firstVBox = (VBox) firstChild;
                firstVBox.setEffect(new InnerShadow(10, Color.RED));
                setDataInHboxDataOfVBoxClickedElement(weatherData);
            }
        }
    }

    private void setDataInHboxDataOfVBoxClickedElement(WeatherData weatherData){
        vboxInstanceData.setText(weatherData.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        vboxInstanceData.setWrapText(true);
        vboxInstancePressure.setText(String.valueOf(weatherData.getPressure()) + " hPa");
        vboxInstanceHumidity.setText(String.valueOf(weatherData.getHumidity()) + "%");
        vboxInstanceWindSpeed.setText(String.valueOf(weatherData.getWindSpeed()) + " m/s");
        vboxInstanceWindMax.setText(String.valueOf(weatherData.getWindDeg()) + "°");
    }

    private void leftCityTextFieldEnterKeyListener() {
        leftCityTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ackLeftLocationButton();
            }
        });
    }

    private Separator addVerticalSeparator(){
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        return separator;
    }

    private void resetStatusLabelAfterDelay(Label label) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> label.setText("")));
        timeline.play();
    }
}
