package pl.gczarny;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pl.gczarny.utils.FxmlUtils;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application
{
    private static final String FXML_MAIN_WINDOW = "/fxml/MainWindow.fxml";
    public static void main(String[] args)
    {
        System.out.println( "Hello World!" );
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Locale.setDefault(new Locale("pl"));
/*        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(FXML_MAIN_WINDOW));
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.messages");
        loader.setResources(bundle);
        Parent parent = loader.load();*/
        SplitPane parent = FxmlUtils.fxmlLoader(FXML_MAIN_WINDOW);
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setMaxWidth(1290);
        stage.setMaxHeight(600);
        stage.setTitle(FxmlUtils.getResourceBundle().getString("title.application"));
        stage.show();

    }
}
