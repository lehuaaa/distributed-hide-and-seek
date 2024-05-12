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
            System.in.read();
            administrationServer.stop(0);
            System.out.println("Server stopped");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}