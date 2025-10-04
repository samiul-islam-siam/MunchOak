module com.example.munchoak {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    requires javafx.base;


    opens com.example.munchoak to javafx.fxml;
    exports com.example.munchoak;
}