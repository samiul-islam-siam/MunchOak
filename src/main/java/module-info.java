module com.example.try2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.try2 to javafx.fxml;
    exports com.example.try2;
}