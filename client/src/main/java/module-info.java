module javafx.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens javafx.client to javafx.fxml;
    exports javafx.client;
    exports javafx.client.UI;
    opens javafx.client.UI to javafx.fxml;
}
