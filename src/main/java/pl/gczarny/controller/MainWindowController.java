package pl.gczarny.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import pl.gczarny.utils.DialogUtils;
import pl.gczarny.utils.FxmlUtils;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    @FXML
    private AnchorPane leftAnchorPane, rightAnchorPane;
    @FXML
    private TextField leftCityTextField, rightCityTextField;
    @FXML private AnchorPane leftNested;
    @FXML NestedController leftNestedController;
    @FXML AnchorPane rightNested;
    @FXML private NestedController rightNestedController;
    private String leftLocation, rightLocation;
    @FXML
    public void ackLeftLocationButton(){
        if(checkLabelEmptyOrEqualToTextField(leftCityTextField, leftLocation)){
            return;
        }
        leftLocation = leftCityTextField.getText();
        leftNestedController.updateStatusLabel(FxmlUtils.getResourceBundle().getString("data.status.request"));
        leftNestedController.fetchForecast(leftLocation, true, true, leftAnchorPane);
    }
    @FXML
    void ackRightLocationButton() {
        if(checkLabelEmptyOrEqualToTextField(rightCityTextField, rightLocation)){
            return;
        }
        rightLocation = rightCityTextField.getText();
        rightNestedController.updateStatusLabel(FxmlUtils.getResourceBundle().getString("data.status.request"));
        rightNestedController.fetchForecast(rightLocation, true, false, rightAnchorPane);
    }
    @FXML
    void refreshLeftButton() {
        if(leftCityTextField.getText().isEmpty() || leftLocation.isEmpty()){
            return;
        }
        leftCityTextField.setText(leftLocation);
        leftNestedController.fetchForecast(leftLocation, true, true, leftAnchorPane);
    }
    @FXML
    void refreshRightButton() {
        if(rightCityTextField.getText().isEmpty() || rightLocation.isEmpty()){
            return;
        }
        rightCityTextField.setText(rightLocation);
        rightNestedController.fetchForecast(rightLocation, true, false, rightAnchorPane);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TextFieldEnterKeyListener(leftCityTextField, this::ackLeftLocationButton, KeyCode.ENTER);
        TextFieldEnterKeyListener(rightCityTextField, this::ackRightLocationButton, KeyCode.ENTER);
        try {
            FxmlUtils.readConfigFile();
            leftLocation = FxmlUtils.getLeftLocation();
            rightLocation = FxmlUtils.getRightLocation();
            leftCityTextField.setText(leftLocation);
            leftNestedController.fetchForecast(leftLocation, false, true, leftAnchorPane);
            rightCityTextField.setText(rightLocation);
            rightNestedController.fetchForecast(rightLocation, false, false, rightAnchorPane);
        } catch (MissingResourceException e) {
            DialogUtils.errorDialog(e.getMessage());
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
    @FXML
    void closeApplication() {
        if(DialogUtils.confirmCloseApplication().get() == ButtonType.OK){
            Platform.exit();
            System.exit(0);
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

    private void TextFieldEnterKeyListener(TextField textField, Runnable action, KeyCode keycode) {
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == keycode) {
                action.run();
            }
        });
    }
}