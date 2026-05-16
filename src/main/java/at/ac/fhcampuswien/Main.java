package at.ac.fhcampuswien;

import at.ac.fhcampuswien.controllers.HelloController;
import at.ac.fhcampuswien.controllers.MovieController;
// neu: databaseutil wird importiert damit initailizedatabase() beim serverstart aufgerufen werden kann
import at.ac.fhcampuswien.database.DatabaseUtil;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private final static int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {

        // neu: databaseutil.initializedatabase() wird als allererstes aufgerufen bevor der server startet
        // diese methode erstellt die movies tabelle falls sie noch nicht existiert (create table if not exists)
        // wenn das nicht hier aufgerufen wird gibt es beim ersten db zugriff einen fehler weil die tabelle fehlt
        DatabaseUtil.initializeDatabase();

        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);

        MovieController movieController = new MovieController();

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