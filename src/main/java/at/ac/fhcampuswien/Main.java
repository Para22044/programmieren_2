package at.ac.fhcampuswien;

import at.ac.fhcampuswien.controllers.HelloController;
import at.ac.fhcampuswien.controllers.MovieController;
import at.ac.fhcampuswien.database.DatabaseUtil;
import at.ac.fhcampuswien.repositories.H2MovieRepository;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.repositories.MovieRepositoryAdapter;
import at.ac.fhcampuswien.services.MovieService;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private final static int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {

        DatabaseUtil.initializeDatabase();
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        MovieRepository movieRepository =
                new MovieRepositoryAdapter(new H2MovieRepository());

        MovieService movieService =
                new MovieService(movieRepository);

        MovieController movieController =
                new MovieController(movieService);

        registerController(server, "/api/hello", new HelloController());
        registerController(server, "/api/movies/getAll", movieController);
        registerController(server, "/api/movies/add", movieController);
        registerController(server, "/api/movies/delete", movieController);
        registerController(server, "/api/movies/update", movieController);
        registerController(server, "/api/movies/search", movieController);

        server.setExecutor(null);
        server.start();
        System.out.printf("Server is running on http://localhost:%d", SERVER_PORT);
    }

    private static void registerController(HttpServer server, String path, HttpHandler handler) {
        HttpContext context = server.createContext(path, handler);
    }
}
