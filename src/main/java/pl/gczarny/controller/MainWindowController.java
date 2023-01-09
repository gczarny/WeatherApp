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
import javafx.scene.control.*;
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
import pl.gczarny.model.*;
import pl.gczarny.utils.DialogUtils;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MainWindowController{
    @FXML
    private AnchorPane leftAnchorPane, rightAnchorPane;
    @FXML
    private TextField leftCityTextField, rightCityTextField;
    @FXML
    private Label statusLeftLabel, statusRightLabel;
    @FXML
    private Label leftCity, rightCity;
    @FXML
    private Label leftPopulation, rightPopulation;
    @FXML
    private Label leftVBoxInstanceData, rightVBoxInstanceData;
    @FXML
    private Label leftVBoxInstanceHumidity, rightVBoxInstanceHumidity;
    @FXML
    private Label leftVBoxInstancePressure, rightVBoxInstancePressure;
    @FXML
    private Label leftVBoxInstanceWindMax, rightVBoxInstanceWindMax;
    @FXML
    private Label leftVBoxInstanceWindSpeed, rightVBoxInstanceWindSpeed;
    @FXML
    private HBox leftHBoxForecast, rightHBoxForecast;
    private String leftLocation, rightLocation;

    @FXML
    public void ackLeftLocationButton(){
        if(checkLabelEmptyOrEqualToTextField(leftCityTextField, leftLocation)){
            return;
        }
        leftLocation = leftCityTextField.getText();
        statusLeftLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.request"));
        fetchForecastLeftData(leftLocation, true);
    }
    @FXML
    void ackRightLocationButton() {
        System.out.println(rightCityTextField.getText() + " " + rightLocation);
        if(checkLabelEmptyOrEqualToTextField(rightCityTextField, rightLocation)){
            return;
        }
        rightLocation = rightCityTextField.getText();
        statusRightLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.request"));
        fetchForecastRightData(rightLocation, true);
    }
    private boolean checkLabelEmptyOrEqualToTextField(TextField textField, String location){
        if(textField.getText().equals(location)){
            return true;
        }
        if(location.isEmpty() || location.equals("")){
            DialogUtils.warningDialog(FxmlUtils.getResourceBundle().getString("warning.empty.field"));
            return true;
        }
        return false;
    }
    @FXML
    void closeApplication() {
        if(DialogUtils.confirmCloseApplication().get() == ButtonType.OK){
            Platform.exit();
            System.exit(0);
        }
    }
    @FXML
    void refreshLeftButton() {
        if(leftCityTextField.getText().isEmpty() || leftLocation.isEmpty()){
            return;
        }
        leftCityTextField.setText(leftLocation);
        fetchForecastLeftData(leftLocation, true);
    }
    @FXML
    void refreshRightButton() {
        if(rightCityTextField.getText().isEmpty() || rightLocation.isEmpty()){
            return;
        }
        rightCityTextField.setText(rightLocation);
        fetchForecastRightData(rightLocation, true);
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
            FxmlUtils.readConfigFile();
            rightLocation = FxmlUtils.getRightLocation();
            rightCityTextField.setText(rightLocation);
            leftLocation = FxmlUtils.getLeftLocation();
            leftCityTextField.setText(leftLocation);
            leftCity.setText(leftLocation);

            rightCity.setText(rightLocation);
            fetchForecastLeftData(leftLocation, false);
            fetchForecastRightData(rightLocation, false);
        } catch (MissingResourceException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
        leftCityTextFieldEnterKeyListener();
    }

    private void fetchForecastLeftData(String location, boolean onButtonDemand){
        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            List<WeatherData> weatherDataList = fetchTask.getValue();
            leftHBoxForecast = displayForecastInHBox(weatherDataList, leftHBoxForecast, true);
            setInnerShadowForFirstVBox(weatherDataList.get(0), leftHBoxForecast, true);
            leftPopulation.setText(Integer.toString(weatherDataList.get(0).getPopulation()));
            WeatherBackgroundManager.changeBackground(weatherDataList.get(0).getId(), leftAnchorPane, LocalTime.now());
            if(weatherDataList.get(0).getPopulation() >= 1000000){
                leftPopulation.setText(">" + weatherDataList.get(0).getPopulation());
            }
            if(onButtonDemand){
                statusLeftLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.done"));
                resetStatusLabelAfterDelay(statusLeftLabel);
                leftCity.setText(weatherDataList.get(0).getLocation());
                FxmlUtils.setLeftLocation(weatherDataList.get(0).getLocation());
            }
        });
        fetchTask.setOnFailed(event -> {
            Throwable exception = fetchTask.getException();
            if(exception instanceof WeatherDataFetchException){
                DialogUtils.errorDialog(exception.getMessage());
                statusLeftLabel.setText("");
            }else {
                exception.printStackTrace();
                DialogUtils.errorDialog(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
            }
        });
        new Thread(fetchTask).start();
    }
    private void fetchForecastRightData(String location, boolean onButtonDemand){
        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            List<WeatherData> weatherDataList = fetchTask.getValue();
            rightHBoxForecast = displayForecastInHBox(weatherDataList, rightHBoxForecast, false);
            setInnerShadowForFirstVBox(weatherDataList.get(0), rightHBoxForecast, false);
            rightPopulation.setText(Integer.toString(weatherDataList.get(0).getPopulation()));
            WeatherBackgroundManager.changeBackground(weatherDataList.get(0).getId(), rightAnchorPane, LocalTime.now());
            if(weatherDataList.get(0).getPopulation() >= 1000000){
                rightPopulation.setText(">" + weatherDataList.get(0).getPopulation());
            }
            if(onButtonDemand){
                statusRightLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.done"));
                resetStatusLabelAfterDelay(statusRightLabel);
                rightCity.setText(weatherDataList.get(0).getLocation());
                FxmlUtils.setRightLocation(weatherDataList.get(0).getLocation());
                FxmlUtils.writeConfigFile();
            }
        });
        fetchTask.setOnFailed(event -> {
            Throwable exception = fetchTask.getException();
            if(exception instanceof WeatherDataFetchException){
                DialogUtils.errorDialog(exception.getMessage());
                statusRightLabel.setText("");
            }else {
                exception.printStackTrace();
                DialogUtils.errorDialog(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
            }
        });
        new Thread(fetchTask).start();
    }

    private ImageView updateImageViews(String id){
        ImageView weatherIcon = new ImageView();
        Image icon = new Image(WeatherIconManager.getIconPath(id));
        weatherIcon.setImage(icon);
        weatherIcon.setFitHeight(Region.USE_COMPUTED_SIZE);
        weatherIcon.setFitWidth(Region.USE_COMPUTED_SIZE);
        return weatherIcon;
    }
    private void makeVBoxClickable(VBox vBox, WeatherData weatherData, HBox hbox, boolean leftSide) {
        vBox.setOnMouseClicked(event -> {
            setInnerShadowEffectOnVBoxAndSetData(vBox, weatherData, leftSide);
            for (Node node : hbox.getChildren()) {
                if (node instanceof VBox && node != vBox) {
                    ((VBox) node).setEffect(null);
                }
            }
        });
    }
    private HBox displayForecastInHBox(List<WeatherData> forecastList, HBox hbox, boolean leftSide) {
        hbox.getChildren().clear();
        //HBoxForecast.setSpacing(30);
        BackgroundFill backgroundFill = new BackgroundFill(new Color(0, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        hbox.setBackground(background);
        for (WeatherData forecastData : forecastList) {
            VBox vBox = getVBox(forecastList, leftSide);
            Label temperatureLabel = new Label(String.format("%.1f°C", forecastData.getTemperature()));
            temperatureLabel.setPrefSize(150, 150);
            temperatureLabel.setTextAlignment(TextAlignment.JUSTIFY);
            temperatureLabel.setAlignment(Pos.CENTER);
            temperatureLabel.setFont(Font.font("Verdana", FontWeight.BOLD,16));
            Label dateLabel = new Label(forecastData.getDateTime().format(DateTimeFormatter.ISO_DATE));
            dateLabel.setTextAlignment(TextAlignment.JUSTIFY);
            dateLabel.setAlignment(Pos.CENTER);
            dateLabel.setFont(Font.font("Verdana", FontWeight.BOLD,12));
            dateLabel.setWrapText(true);
            vBox.getChildren().addAll(temperatureLabel, dateLabel, new Separator(), updateImageViews(forecastData.getIcon()));
            vBox.getProperties().put("weatherData", forecastData);
            makeVBoxClickable(vBox, forecastData, hbox, leftSide);
            vBox.setCursor(Cursor.HAND);
            hbox.getChildren().addAll(vBox, addVerticalSeparator());
            HBox.setHgrow(vBox, Priority.ALWAYS);
        }
        return hbox;
    }

    private void setInnerShadowForFirstVBox(WeatherData weatherData, HBox hbox, boolean leftSide) {
        if (hbox.getChildren().size() > 0) {
            Node firstChild = hbox.getChildren().get(0);
            if (firstChild instanceof VBox) {
                VBox firstVBox = (VBox) firstChild;
                setInnerShadowEffectOnVBoxAndSetData(firstVBox, weatherData, leftSide);
            }
        }
    }

    private void setInnerShadowEffectOnVBoxAndSetData(VBox vbox, WeatherData weatherData, boolean leftSide){
        vbox.setEffect(new InnerShadow(10, Color.RED));
        if(leftSide){
            setLeftDataInHboxDataTakenFromVBoxClicked(weatherData);
        }else{
            setRightDataInHboxDataTakenFromVBoxClicked(weatherData);
        }
    }

    private void setLeftDataInHboxDataTakenFromVBoxClicked(WeatherData weatherData){
        leftVBoxInstanceData.setText(weatherData.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        leftVBoxInstanceData.setWrapText(true);
        leftVBoxInstancePressure.setText(String.valueOf(weatherData.getPressure()) + " hPa");
        leftVBoxInstanceHumidity.setText(String.valueOf(weatherData.getHumidity()) + "%");
        leftVBoxInstanceWindSpeed.setText(String.valueOf(weatherData.getWindSpeed()) + " m/s");
        leftVBoxInstanceWindMax.setText(String.valueOf(weatherData.getWindDeg()) + "°");
    }
    private void setRightDataInHboxDataTakenFromVBoxClicked(WeatherData weatherData){
        rightVBoxInstanceData.setText(weatherData.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        rightVBoxInstanceData.setWrapText(true);
        rightVBoxInstancePressure.setText(String.valueOf(weatherData.getPressure()) + " hPa");
        rightVBoxInstanceHumidity.setText(String.valueOf(weatherData.getHumidity()) + "%");
        rightVBoxInstanceWindSpeed.setText(String.valueOf(weatherData.getWindSpeed()) + " m/s");
        rightVBoxInstanceWindMax.setText(String.valueOf(weatherData.getWindDeg()) + "°");
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

    private VBox getVBox(List<WeatherData> forecastList, boolean leftSide){
        VBox vBox = new VBox();
        if(leftSide){
            vBox.setPrefWidth(leftHBoxForecast.getWidth() / forecastList.size());
        } else{
            vBox.setPrefWidth(rightHBoxForecast.getWidth() / forecastList.size());
        }
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    private void resetStatusLabelAfterDelay(Label label) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> label.setText("")));
        timeline.play();
    }
}
