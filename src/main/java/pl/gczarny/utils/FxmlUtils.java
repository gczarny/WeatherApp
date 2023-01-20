package pl.gczarny.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/*
** Responsible for loading all fxml files in app
 */
public class FxmlUtils {

    private static final String CONFIG_FILE_NAME = "/config.json";
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
            InputStream input = FxmlUtils.class.getResourceAsStream(CONFIG_FILE_NAME);
            JSONObject config = new JSONObject(new JSONTokener(input));
            leftLocation = config.getString(LEFT_LOCATION_PROPERTY);
            rightLocation = config.getString(RIGHT_LOCATION_PROPERTY);
            input.close();
        } catch (IOException | JSONException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
    }

    public static void writeConfigFile() {
        try {
            URL url = FxmlUtils.class.getResource(CONFIG_FILE_NAME);
            File file = new File(url.getFile());
            JSONObject config = new JSONObject();
            config.put(LEFT_LOCATION_PROPERTY, leftLocation);
            config.put(RIGHT_LOCATION_PROPERTY, rightLocation);
            FileWriter writer = new FileWriter(file);
            writer.write(config.toString(2));
            writer.flush();
            writer.close();
        } catch (IOException | JSONException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
    }
}

