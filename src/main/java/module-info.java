module WeatherApp {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires com.google.gson;
    requires com.google.common;

    opens pl.gczarny to javafx.fxml;
    exports pl.gczarny;
    exports pl.gczarny.controller;
    opens pl.gczarny.controller to javafx.fxml;
    opens pl.gczarny.utils;

}