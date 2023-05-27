module com.example.lab1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires junit;
    requires lombok;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.example.lab1 to javafx.fxml;
    exports com.example.lab1;
}