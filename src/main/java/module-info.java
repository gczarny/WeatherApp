module WeatherApp {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires com.google.gson;
    requires com.google.common;
    requires org.json;

    exports pl.gczarny;
    exports pl.gczarny.controller;
    opens pl.gczarny.controller to javafx.fxml;
    opens pl.gczarny.utils;
    opens pl.gczarny;

}