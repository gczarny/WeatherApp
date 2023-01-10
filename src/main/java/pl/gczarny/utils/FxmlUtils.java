package pl.gczarny.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ResourceBundle;

/*
** Responsible for loading all fxml files in our app
 */
public class FxmlUtils {

    private static final String CONFIG_FILE_NAME = "src/main/resources/config.json";
    private static final String LEFT_LOCATION_PROPERTY = "leftLocation";
    private static final String RIGHT_LOCATION_PROPERTY = "rightLocation";
    private static String leftLocation;
    private static String rightLocation;

    public static SplitPane fxmlLoader(String fxmlPath){
        FXMLLoader loader = new FXMLLoader(FxmlUtils.class.getResource(fxmlPath));
        loader.setResources(getResourceBundle());
        try {
            return loader.load();
        } catch (Exception e) {
            DialogUtils.errorDialog(e.getMessage());
        }
        return null;

    }

    public static ResourceBundle getResourceBundle(){
        return ResourceBundle.getBundle("bundles.messages");
    }

/*    public static String getLeftLocation() {
        JSONObject config = readConfigFile();
        if (config.has(LEFT_LOCATION_PROPERTY)) {
            return config.getString(LEFT_LOCATION_PROPERTY);
        }
        return "";
    }

    public static String getRightLocation() {
        JSONObject config = readConfigFile();
        if (config.has(RIGHT_LOCATION_PROPERTY)) {
            return config.getString(RIGHT_LOCATION_PROPERTY);
        }
        return "";
    }

    public static void setLeftLocation(String leftLocation) {
        JSONObject config = readConfigFile();
        config.put(LEFT_LOCATION_PROPERTY, leftLocation);
        writeConfigFile(config);
    }
    public static void setRightLocation(String leftLocation) {
        JSONObject config = readConfigFile();
        config.put(RIGHT_LOCATION_PROPERTY, leftLocation);
        writeConfigFile(config);
    }

    private static JSONObject readConfigFile(){
        JSONObject config = new JSONObject();
        try(FileReader fileReader = new FileReader(CONFIG_FILE_NAME)){
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while(line != null){
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            config = new JSONObject(stringBuilder.toString());
        } catch (IOException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
        return config;
    }

    private static void writeConfigFile(JSONObject config) {
        try {
            File configFile = new File(CONFIG_FILE_NAME);
            FileWriter fileWriter = new FileWriter(configFile);
            fileWriter.write(config.toString(4));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    public static String getLeftLocation() {
        return leftLocation;
    }

    public static void setLeftLocation(String leftLocation) {
        FxmlUtils.leftLocation = leftLocation;
    }

    public static String getRightLocation() {
        return rightLocation;
    }

    public static void setRightLocation(String rightLocation) {
        FxmlUtils.rightLocation = rightLocation;
    }

    public static void readConfigFile() {
        try {
            // odczytaj plik config.json
            FileReader reader = new FileReader(CONFIG_FILE_NAME);
            JSONObject config = new JSONObject(new JSONTokener(reader));

            // przypisz wartości do pól leftLocation i rightLocation
            leftLocation = config.getString(LEFT_LOCATION_PROPERTY);
            rightLocation = config.getString(RIGHT_LOCATION_PROPERTY);
        } catch (IOException | JSONException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
    }

    public static void writeConfigFile() {
        try {
            // utwórz obiekt JSON zawierający wartości pól leftLocation i rightLocation
            JSONObject config = new JSONObject();
            config.put(LEFT_LOCATION_PROPERTY, leftLocation);
            config.put(RIGHT_LOCATION_PROPERTY, rightLocation);

            // zapisz obiekt JSON do pliku config.json
            FileWriter writer = new FileWriter(CONFIG_FILE_NAME);
            writer.write(config.toString(2));
            writer.close();
        } catch (IOException | JSONException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
    }
}

