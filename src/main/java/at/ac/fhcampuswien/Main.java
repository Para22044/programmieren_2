package at.ac.fhcampuswien;

import at.ac.fhcampuswien.controllers.HelloController;
import at.ac.fhcampuswien.controllers.MovieController;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private final static int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {
        // Create an HTTP server listening on defined port
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);

        //Controller
        MovieController movieController = new MovieController();

        // Register controllers and their handlers - REST endpoints
        registerController(server, "/api/hello", new HelloController());

        registerController(server, "/api/movies/getAll", movieController);
        registerController(server, "/api/movies/add", movieController);
        registerController(server, "/api/movies/delete", movieController);
        registerController(server, "/api/movies/update", movieController);
        registerController(server, "/api/movies/search", movieController);

        // Start server
        server.setExecutor(null);
        server.start();
        System.out.printf("Server is running on http://localhost:%d", SERVER_PORT);
    }

    private static void registerController(HttpServer server, String path, HttpHandler handler) {
        HttpContext context = server.createContext(path, handler);
    }
}