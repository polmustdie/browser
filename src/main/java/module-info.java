module com.example.web1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.google.gson;


    opens com.example.web1 to javafx.fxml;
    exports com.example.web1;
}