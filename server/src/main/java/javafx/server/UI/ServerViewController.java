package javafx.server.UI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * ServerViewController Class
 * JavaFX multithreaded server using sockets
 *
 * @version 0.1
 * @since 2022-04-08
 */
public class ServerViewController implements Initializable {

    private static final DateFormat sfd = new SimpleDateFormat("EEEE MMMM dd hh:mm a 'EST'");
    static List<ThreadClient> clients = new ArrayList<>();
    private static final Date date = new Date();
    public static ServerSocket ss = null;

    @FXML
    private TextArea console;

    /**
     * Define new ServerSocket running on port 6000
     * Broadcast server start through local date/time (EST)
     * Implemented using error handling
     */
    private void start() {
        try {
            ss = new ServerSocket(6000);
            show("Server started at " + sfd.format(date));
        } catch (IOException ex) {
            close();
        }
    }

    public static void close() {
        try {
            ss.close();
        } catch (IOException ignored) {}

        Platform.exit();
        System.exit(0);
    }

    /**
     * Accept new client connection via ServerSocket
     * Broadcast connection using socket properties (address, port/localport)
     * Create and add client to multithreaded collection
     * Implemented using error handling
     */
    private void accept() {
        try {
            while (ss != null) {
                Socket socket = ss.accept();
                show("Connection from " + socket + " at " + sfd.format(date));
                new Thread(new ThreadClient(socket)).start();
                clients.add(new ThreadClient(socket));
            }
        } catch (IOException ex) {
            show(ex.toString());
        }
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
     * Create a new thread for starting server and accepting clients
     * @param location unused
     * @param resources unused
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            start();
            accept();
        }).start();
    }

    /**
     * ThreadClient Class
     * Handle data sent to and from client socket
     *
     * @version 0.1
     * @since 2022-04-08
     */
    class ThreadClient implements Runnable {

        private final Socket socket;

        /**
         * Overloaded constructor
         * @param socket
         */
        public ThreadClient(Socket socket) {
            this.socket = socket;
        }

        /**
         * Receive client message via socket input stream
         * Parse message as string in UTF format
         * Iterate through client collection and re-transmit message for all connected clients to view
         * Broadcast client message locally using TextArea
         * Implemented using error handling
         */
        @Override
        public void run() {
            try {
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                while (true) {
                    String msg = inputFromClient.readUTF();
                    for (ThreadClient client : clients) {
                        client.send(msg);
                    }
                    show(msg);
                }
            } catch (Exception ex) {
                show(ex.toString());
            }
        }

        /**
         * Re-transmit message sent from individual client
         * Used in ThreadClient Runnable function
         * Implemented using error handling
         * @param msg information to send back
         * @throws IOException used to catch stream errors
         */
        private void send(String msg) throws IOException {
            DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
            outputToClient.writeUTF(msg);
            outputToClient.flush();
        }
    }
}
