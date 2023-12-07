module com.example.project_9 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.project_9 to javafx.fxml;
    exports com.example.project_9;
}