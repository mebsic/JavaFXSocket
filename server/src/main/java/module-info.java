module javafx.server {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens javafx.server to javafx.fxml;
    exports javafx.server;
    exports javafx.server.UI;
    opens javafx.server.UI to javafx.fxml;
}
