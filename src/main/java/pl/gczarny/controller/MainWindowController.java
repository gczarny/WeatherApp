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
import pl.gczarny.controller.Tasks.WeatherDataFetchTask;
import pl.gczarny.model.*;
import pl.gczarny.utils.DialogUtils;
import pl.gczarny.utils.FxmlUtils;
import pl.gczarny.utils.exceptions.WeatherDataFetchException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.MissingResourceException;

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
    private Label leftVBoxInstanceDate, rightVBoxInstanceDate;
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
        fetchForecast(leftLocation, true, true, leftPopulation, leftAnchorPane, statusLeftLabel, leftCity);
    }
    @FXML
    void ackRightLocationButton() {
        if(checkLabelEmptyOrEqualToTextField(rightCityTextField, rightLocation)){
            return;
        }
        rightLocation = rightCityTextField.getText();
        statusRightLabel.setText(FxmlUtils.getResourceBundle().getString("data.status.request"));
        fetchForecast(rightLocation, true, false, rightPopulation, rightAnchorPane, statusRightLabel,rightCity);
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
        fetchForecast(leftLocation, true, true, leftPopulation, leftAnchorPane, statusLeftLabel, leftCity);
    }
    @FXML
    void refreshRightButton() {
        if(rightCityTextField.getText().isEmpty() || rightLocation.isEmpty()){
            return;
        }
        rightCityTextField.setText(rightLocation);
        fetchForecast(rightLocation, true, false, rightPopulation, rightAnchorPane, statusRightLabel,rightCity);
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
        TextFieldEnterKeyListener(leftCityTextField, this::ackLeftLocationButton);
        TextFieldEnterKeyListener(rightCityTextField, this::ackRightLocationButton);
        try {
            FxmlUtils.readConfigFile();
            rightLocation = FxmlUtils.getRightLocation();
            rightCityTextField.setText(rightLocation);
            rightCity.setText(rightLocation);
            leftLocation = FxmlUtils.getLeftLocation();
            leftCityTextField.setText(leftLocation);
            leftCity.setText(leftLocation);
            fetchForecast(leftLocation, false, true, leftPopulation, leftAnchorPane, statusLeftLabel, leftCity);
            fetchForecast(rightLocation, false, false, rightPopulation, rightAnchorPane, statusRightLabel,rightCity);
        } catch (MissingResourceException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
    }

    private void fetchForecast(String location, boolean onButtonDemand, boolean leftSide, Label population, Pane pane, Label status, Label city){
        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            List<WeatherData> weatherDataList = fetchTask.getValue();
            if(leftSide){
                leftHBoxForecast = displayForecastInHBox(weatherDataList, leftHBoxForecast, leftSide);
            }else{
                rightHBoxForecast = displayForecastInHBox(weatherDataList, rightHBoxForecast, leftSide);
            }
            setPopulationLabel(population, weatherDataList.get(0).getPopulation());
            WeatherBackgroundManager.changeBackground(weatherDataList.get(0).getId(), pane, LocalTime.now());
            if(onButtonDemand){
                btnDemandActionOnSucceededEvent(status, city, weatherDataList.get(0).getLocation());
                if(leftSide){
                    FxmlUtils.setLeftLocation(weatherDataList.get(0).getLocation());
                }else{
                    FxmlUtils.setRightLocation(weatherDataList.get(0).getLocation());
                }
                FxmlUtils.writeConfigFile();
            }
        });
        fetchTask.setOnFailed(event -> {
            setOnFailedEvent(fetchTask, status);
        });
        new Thread(fetchTask).start();
    }

    private void setPopulationLabel(Label label, int population){
        label.setText(Integer.toString(population));
        if(population >= 1000000){
            label.setText(">" + population);
        }
    }
    private void btnDemandActionOnSucceededEvent(Label status, Label city, String location){
        resetStatusLabelAfterDelay(status);
        city.setText(location);
        status.setText(FxmlUtils.getResourceBundle().getString("data.status.done"));

    }

    private void setOnFailedEvent(WeatherDataFetchTask task, Label label){
        Throwable exception = task.getException();
        if(exception instanceof WeatherDataFetchException){
            DialogUtils.errorDialog(exception.getMessage());
            label.setText("");
        }else {
            exception.printStackTrace();
            DialogUtils.errorDialog(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        }
    }

    private boolean checkLabelEmptyOrEqualToTextField(TextField cityTextField, String location){
        if(cityTextField.getText().isEmpty() || cityTextField.getText().equals("")){
            DialogUtils.warningDialog(FxmlUtils.getResourceBundle().getString("warning.empty.field"));
            return true;
        }
        if(cityTextField.getText().equals(location)){
            return true;
        }
        return false;
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
        BackgroundFill backgroundFill = new BackgroundFill(new Color(0, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        hbox.setBackground(background);
        for (WeatherData forecastData : forecastList) {
            VBox vBox = getVBox(forecastList, leftSide, hbox);
            Label temperatureLabel = createLabelWithPrefSize(String.format("%.1f°C", forecastData.getTemperature()), true,
                    150, 150, 12);
            Label dateLabel = createLabel(String.format("%.1f°C", forecastData.getTemperature()), 15, "Verdana");
            vBox.getChildren().addAll(temperatureLabel, dateLabel, new Separator(), updateImageViews(forecastData.getIcon()));
            vBox.getProperties().put("weatherData", forecastData);
            makeVBoxClickable(vBox, forecastData, hbox, leftSide);
            vBox.setCursor(Cursor.HAND);
            hbox.getChildren().addAll(vBox, addVerticalSeparator());
            HBox.setHgrow(vBox, Priority.ALWAYS);
        }
        setInnerShadowForFirstVBox(forecastList.get(0), hbox, leftSide);
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
            setDataInHBoxTakenFromClickedVBox(weatherData, leftVBoxInstanceDate, leftVBoxInstancePressure, leftVBoxInstanceHumidity,
                    leftVBoxInstanceWindSpeed, leftVBoxInstanceWindMax);
        }else{
            setDataInHBoxTakenFromClickedVBox(weatherData, rightVBoxInstanceDate, rightVBoxInstancePressure, rightVBoxInstanceHumidity,
                    rightVBoxInstanceWindSpeed, rightVBoxInstanceWindMax);
        }
    }
    private void setDataInHBoxTakenFromClickedVBox(WeatherData weatherData, Label date, Label pressure, Label humidity, Label windSped, Label windMax){
        date.setText(weatherData.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        date.setWrapText(true);
        pressure.setText(weatherData.getPressure() + " hPa");
        humidity.setText(weatherData.getHumidity() + "%");
        windSped.setText(weatherData.getWindSpeed() + " m/s");
        windMax.setText(weatherData.getWindDeg() + "°");
    }
    private void TextFieldEnterKeyListener(TextField textField, Runnable action) {
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                action.run();
            }
        });
    }
    private Separator addVerticalSeparator(){
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        return separator;
    }
    private VBox getVBox(List<WeatherData> forecastList, boolean leftSide, HBox hbox){
        VBox vBox = new VBox();
        if(leftSide){
            vBox.setPrefWidth(hbox.getWidth() / forecastList.size());
        } else{
            vBox.setPrefWidth(hbox.getWidth() / forecastList.size());
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
    private Label createLabel(String text, int fontSize, String fontFamily){
        Label label = new Label(text);
        label.setTextAlignment(TextAlignment.JUSTIFY);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font(fontFamily, FontWeight.BOLD,fontSize));
        label.setWrapText(true);
        return label;
    }
    private Label createLabelWithPrefSize(String text, boolean setPrefSize, double prefWidth, double prefHeight, int fontSize){
        Label label = new Label(text);
        if(setPrefSize){
            label.setPrefSize(prefWidth, prefHeight);
        }
        label.setTextAlignment(TextAlignment.JUSTIFY);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("Verdana", FontWeight.BOLD,fontSize));
        label.setWrapText(true);
        return label;
    }
}

/*    private void fetchForecastLeftData(String location, boolean onButtonDemand){
        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            List<WeatherData> weatherDataList = fetchTask.getValue();
            leftHBoxForecast = displayForecastInHBox(weatherDataList, leftHBoxForecast, true);
            setPopulationLabel(leftPopulation, weatherDataList.get(0).getPopulation());
            WeatherBackgroundManager.changeBackground(weatherDataList.get(0).getId(), leftAnchorPane, LocalTime.now());

            if(onButtonDemand){
                btnDemandActionOnSucceededEvent(statusLeftLabel, leftCity, weatherDataList.get(0).getLocation());
                FxmlUtils.setLeftLocation(weatherDataList.get(0).getLocation());
                FxmlUtils.writeConfigFile();
            }
        });
        fetchTask.setOnFailed(event -> {
            setOnFailedEvent(fetchTask, statusLeftLabel);
        });
        new Thread(fetchTask).start();
    }
    private void fetchForecastRightData(String location, boolean onButtonDemand){
        WeatherDataFetchTask fetchTask = new WeatherDataFetchTask(location);
        fetchTask.setOnSucceeded(event -> {
            List<WeatherData> weatherDataList = fetchTask.getValue();

            setPopulationLabel(rightPopulation, weatherDataList.get(0).getPopulation());
            WeatherBackgroundManager.changeBackground(weatherDataList.get(0).getId(), rightAnchorPane, LocalTime.now());
            if(onButtonDemand){
                btnDemandActionOnSucceededEvent(statusRightLabel, rightCity, weatherDataList.get(0).getLocation());
                FxmlUtils.setRightLocation(weatherDataList.get(0).getLocation());
                FxmlUtils.writeConfigFile();
            }
        });
        fetchTask.setOnFailed(event -> {
            setOnFailedEvent(fetchTask, statusRightLabel);
        });
        new Thread(fetchTask).start();
    }*/