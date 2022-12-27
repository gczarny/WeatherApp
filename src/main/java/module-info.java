module WeatherApp {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;

    opens pl.gczarny to javafx.fxml;
    exports pl.gczarny;
    exports pl.gczarny.view;
    opens pl.gczarny.view to javafx.fxml;


}