package pl.gczarny.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;

import java.io.*;
import java.util.Properties;
import java.util.ResourceBundle;

/*
** Responsible for loading all fxml files in our app
 */
public class FxmlUtils {

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

    public static void setConfigResourceProperty(String key, String value){
        try (OutputStream output = new FileOutputStream("src/main/resources/bundles/config.properties")) {
            Properties properties = new Properties();
            properties.setProperty(key, value);
            properties.store(output, null);
        } catch(IOException e){
            DialogUtils.errorDialog(e.getMessage());
        }
    }

    public static String getConfigResourceProperty(String key){
        try(InputStream input = new FileInputStream("src/main/resources/bundles/config.properties")){
            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty(key);
        } catch(IOException e){
            DialogUtils.errorDialog(e.getMessage());
        }
        return null;
    }
}
