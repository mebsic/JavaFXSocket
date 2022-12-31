package javafx.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * ClientApplication Class
 *
 * @version 0.1
 * @since 2022-04-07
 */
public class ClientApplication extends Application {

    /**
     * Create the scene using client-view (fxml loader)
     * Sets stage properties and close request event handler
     * Update window title to generated client display name
     * Implemented using error handling
     *
     * @param stage used in window layout
     * @throws Exception used to catch fxml errors
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.setScene(scene);
        if (ClientViewController.getDisplayName().isEmpty()) {
            // connection error, could not set display name for client
            stage.hide();
        } else {
            stage.show();
        }
        stage.setOnCloseRequest(e -> ClientViewController.close());
        Platform.runLater(() -> stage.setTitle(ClientViewController.getDisplayName() + " - type q to exit"));
    }

    /**
     * Invoke function to launch client application
     *
     * @param args unused
     */
    public static void main(String[] args) {
        launch();
    }
}
