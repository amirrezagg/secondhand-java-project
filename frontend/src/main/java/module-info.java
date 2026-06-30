module ca.aut.secondhand.frontend {
    requires javafx.controls;
    requires javafx.fxml;


    opens ca.aut.secondhand.frontend to javafx.fxml;
    exports ca.aut.secondhand.frontend;
}