module ir.aut.secondhand.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;


    opens ir.aut.secondhand.frontend to javafx.fxml,
            com.fasterxml.jackson.databind;

    opens ir.aut.secondhand.frontend.dto to com.fasterxml.jackson.databind;
    exports ir.aut.secondhand.frontend;
}