package pl.gczarny;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import pl.gczarny.utils.FxmlUtils;


import java.util.Locale;

public class App extends Application
{
    private static final String FXML_MAIN_WINDOW = "/fxml/MainWindow.fxml";
    public static void main(String[] args)
    {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Locale.setDefault(new Locale("pl"));
        SplitPane parent = FxmlUtils.fxmlLoader(FXML_MAIN_WINDOW);
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setMaxWidth(1290);
        stage.setMaxHeight(600);
        stage.setTitle(FxmlUtils.getResourceBundle().getString("title.application"));
        stage.show();
    }
}
