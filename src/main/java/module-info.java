module com.example.munchoak {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.munchoak to javafx.fxml;
    opens com.example.login to javafx.fxml;

    exports com.example.munchoak;
    exports com.example.login;
}
