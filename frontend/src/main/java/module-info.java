module ir.aut.secondhand.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens ir.aut.secondhand.frontend to javafx.fxml;
    exports ir.aut.secondhand.frontend;
}