package administration.server;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public class AdministrationServer {

    private final static String HOST = "localhost";
    private final static int PORT = 8080;

    public static void main(String[] args) {
        try {
            HttpServer administrationServer = HttpServerFactory.create("http://" + HOST + ":" + PORT + "/");
            administrationServer.start();
            System.out.println("Server is running on: http://" + HOST + ":" + PORT);
            System.out.println("Press any key + ENTER, to stop the server...");
            System.in.read();
            administrationServer.stop(0);
            System.out.println("The server is shutting down.");
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}