package javafx.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.server.UI.ServerViewController;
import javafx.stage.Stage;

/**
 * ServerApplication Class
 * Model JavaFX scene and stage
 *
 * @version 0.1
 * @since 2022-04-07
 */
public class ServerApplication extends Application {

    /**
     * Create the scene using server-view (fxml loader)
     * Sets stage properties and close request event handler
     * Implemented using error handling
     * @param stage used in window layout
     * @throws Exception used to catch fxml errors
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Server");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> ServerViewController.close());
    }

    /**
     * Invoke function to launch server application
     * @param args unused
     */
    public static void main(String[] args) {
        launch();
    }
}
