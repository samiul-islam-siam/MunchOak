module com.example.munchoak {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.munchoak to javafx.fxml;
    opens com.example.login to javafx.fxml;

    exports com.example.munchoak;
    exports com.example.login;
    exports com.example.view;
    opens com.example.view to javafx.fxml;
    exports com.example.manager;
    opens com.example.manager to javafx.fxml;

    opens com.example.network to javafx.fxml; // newly added
    exports com.example.network;
    exports com.example.menu;
    opens com.example.menu to javafx.fxml;
}
