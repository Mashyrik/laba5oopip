module com.example.laba5 {
    requires javafx.controls;
    requires javafx.fxml;



    opens com.example.laba5 to javafx.fxml;
    exports com.example.laba5;
}