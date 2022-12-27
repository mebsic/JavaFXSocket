package javafx.client.UI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * ClientViewController Class
 * JavaFX multithreaded client using sockets
 *
 * @version 0.1
 * @since 2022-04-08
 */
public class ClientViewController implements Initializable {

    private static final StringBuilder username = new StringBuilder("Client");
    private static final String disconnectMsg = getDisplayName() + " left.";
    private static final String connectMsg = getDisplayName() + " joined.";
    private static DataOutputStream toServer = null;
    public static Socket socket = null;

    @FXML
    private TextArea console;

    @FXML
    private Button send;

    @FXML
    private TextField input;

    /**
     * Attempt establishing connection to server socket on port 6000
     * Set client display name and broadcast joined
     * Event handlers used in sending message
     * Implemented using error handling
     */
    private void connect() {
        try {
            socket = new Socket("localhost", 6000);
            toServer = new DataOutputStream(socket.getOutputStream());
            setDisplayName();
            toServer.writeUTF(connectMsg);
            toServer.flush();
            send.setOnAction(e -> send());
            input.setOnAction(e -> send());
        } catch (Exception ex) {
            alert();
        }
    }

    private void alert() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);

            alert.setTitle("Error");
            alert.setHeaderText("Could not connect to the server!");
            alert.setContentText("Please try again later");

            alert.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    close();
                }
            });
        });
    }

    public static void close() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Broadcast message back to client via server socket input stream
     * Data parsed as string in UTF format
     * Implemented using error handling
     */
    private void listen() {
        try {
            while (socket != null) {
                DataInputStream fromServer = new DataInputStream(socket.getInputStream());
                String msg = fromServer.readUTF();
                show(msg);
            }
        } catch (Exception ex) {
            if (ex.toString().startsWith("java.net.SocketException")) {
                close();
            } else {
                show(ex.toString());
            }
        }
    }

    /**
     * Send a message to all connected clients via socket output stream
     * Concatenate client display name with text from input field
     * Client disconnects from server by typing "q" or closing window
     * Nothing happens when message to send is empty
     * Send message to server as string in UTF format and reset input field
     * Implemented using error handling and input validation
     */
    private void send() {
        try {
            String msg = getDisplayName() + ": " + input.getText();
            toServer = new DataOutputStream(socket.getOutputStream());

            if (input.getText().equals("q")) {
                toServer.writeUTF(disconnectMsg);
                toServer.flush();
                close();
            } else if (input.getText().isEmpty()) {
                // do nothing...
            } else {
                toServer.writeUTF(msg);
                toServer.flush();
                input.clear();
            }
        } catch (Exception ex) {
            show(ex.toString());
        }
    }

    /**
     * Set client display name by concatenating random number
     * Used to make each client distinct
     */
    private static void setDisplayName() {
        username.append(getRandomNumber(10, 100));
    }

    /**
     * Retrieve randomly-generated client display name
     * @return client username
     */
    public static String getDisplayName() {
        return username.toString();
    }

    /**
     * Generate random number within a specified range
     * Used for setting new client display name
     * @param min used to set minimum value
     * @param max used to set maximum value
     * @return random number
     */
    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    /**
     * Display message by appending to TextArea
     * Implemented using Platform
     * @param msg information to broadcast
     */
    private void show(String msg) {
        Platform.runLater(() -> console.appendText(msg + "\n"));
    }

    /**
     * Create a new thread for connecting client and exchanging messages
     * @param location unused
     * @param resources unused
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            connect();
            listen();
        }).start();
    }
}
