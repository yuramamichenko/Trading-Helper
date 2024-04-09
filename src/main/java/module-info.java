module com.example.myfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.net.http;

    opens app to javafx.fxml;
    exports app;
    exports app.FXparts;
    opens app.FXparts to javafx.fxml;
}