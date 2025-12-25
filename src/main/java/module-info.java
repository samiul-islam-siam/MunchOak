module com.munchoak.mainpage {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;

    opens com.munchoak.mainpage to javafx.fxml;
    opens com.munchoak.authentication to javafx.fxml;

    exports com.munchoak.mainpage;
    exports com.munchoak.authentication;
    exports com.munchoak.reservation;
    opens com.munchoak.reservation to javafx.fxml;
    exports com.munchoak.manager;
    opens com.munchoak.manager to javafx.fxml;

    opens com.munchoak.network to javafx.fxml; // newly added
    exports com.munchoak.network;
    exports com.munchoak.menu;
    opens com.munchoak.menu to javafx.fxml;
    exports com.munchoak.homepage;
    opens com.munchoak.homepage to javafx.fxml;
    exports com.munchoak.payment;
    opens com.munchoak.payment to javafx.fxml;
    exports com.munchoak.server;
    opens com.munchoak.server to javafx.fxml;
    exports com.munchoak.coupon;
    opens com.munchoak.coupon to javafx.fxml;
    exports com.munchoak.cart;
    opens com.munchoak.cart to javafx.fxml;
}
