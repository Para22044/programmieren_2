package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.repositories.MovieRepository;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.ApiUtils;

import java.io.*;
import java.util.*;

import com.google.gson.Gson;
import at.ac.fhcampuswien.services.MovieService;

public class MovieController implements HttpHandler {

    // neu: movieservice bekommt jetzt ein new movierepository() reingegeben
    // weil der movieservice konstruktor jetzt ein movierepository erwartet statt einer list<movie>
    private final MovieService movieService = new MovieService(new MovieRepository());
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // neu: der gesamte routing block ist jetzt in einem try-catch block gewrapped
        // alle exceptions die in den privaten handler methoden geworfen werden landen hier
        // das bedeutet man muss exceptions nicht in jeder einzelnen methode fangen - zentrales exception handling
        try {
            if (path.endsWith("/getAll")) {
                handleGetAll(exchange, method);
            } else if (path.endsWith("/add")) {
                handleAdd(exchange, method);
            } else if (path.endsWith("/delete")) {
                handleDelete(exchange, method);
            } else if (path.endsWith("/update")) {
                handleUpdate(exchange, method);
            } else if (path.endsWith("/search")) {
                handleSearch(exchange, method);
            } else {
                ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Path not found\" }");
            }
            // neu: databaseexception wird mit http 500 beantwortet - interner serverfehler weil die db nicht erreichbar ist oder sql fehlschlägt
        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"" + e.getMessage() + "\" }");
            // neu: movienotfoundexception wird mit http 404 beantwortet - der angeforderte film existiert nicht in der db
        } catch (MovieNotFoundException e) {
            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"" + e.getMessage() + "\" }");
            // neu: jsonsyntaxexception wird mit http 400 beantwortet - der client hat ungültiges json geschickt
            // gson wirft diese exception automatisch wenn fromjson() ein malformed json bekommt
        } catch (JsonSyntaxException e) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Malformed JSON\" }");
            // neu: allgemeiner fallback für alle anderen unerwarteten exceptions - http 500
        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"Unexpected server error\" }");
        }
    }

    private void handleGetAll(HttpExchange exchange, String method) throws IOException, DatabaseException {

        if (!method.equals("GET")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        List<Movie> allMovies = movieService.getAllMovies();
        String json = gson.toJson(allMovies);

        ApiUtils.sendResponse(exchange, 200, json);
    }

    private void handleAdd(HttpExchange exchange, String method) throws IOException, DatabaseException {

        if (!method.equals("POST")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = readBody(exchange);
        Movie newMovie = gson.fromJson(body, Movie.class);

        if (newMovie == null) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            return;
        }

        boolean success = movieService.addMovie(newMovie);
        if (!success) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Movie already exists\" }");
            return;
        }

        ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");
    }

    // neu: throws movienotfoundexception hinzugefügt weil movieservice.deletemovie() diese exception weitergibt
    // wenn das repository keine zeile löscht (film existiert nicht) kommt movienotfoundexception hoch
    // die exception wird nicht hier gefangen sondern geht weiter zum zentralen try-catch in handle()
    private void handleDelete(HttpExchange exchange, String method) throws IOException, DatabaseException, MovieNotFoundException {

        if (!method.equals("DELETE")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = readBody(exchange);
        Movie toDelete = gson.fromJson(body, Movie.class);

        if (toDelete == null) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            return;
        }

        // neu: kein boolean check mehr nötig - wenn film nicht gefunden wird wirft das repository selbst eine movienotfoundexception
        // die exception propagiert durch movieservice.deletemovie() direkt hierher und dann weiter zu handle()
        movieService.deleteMovie(toDelete);
        ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");
    }

    // neu: throws movienotfoundexception hinzugefügt - gleiche begründung wie bei handledelete
    // neu: der innere try-catch wurde entfernt - exception handling passiert jetzt zentral in handle()
    private void handleUpdate(HttpExchange exchange, String method) throws IOException, DatabaseException, MovieNotFoundException {

        if (!method.equals("PUT")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = readBody(exchange);
        Movie updatedMovie = gson.fromJson(body, Movie.class);

        if (updatedMovie == null) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            return;
        }

        // neu: kein boolean check mehr - wenn id nicht gefunden wird kommt movienotfoundexception vom repository
        // propagiert durch movieservice.updatemovie() und wird in handle() als 404 beantwortet
        movieService.updateMovie(updatedMovie);
        ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie updated successfully\" }");
    }

    private void handleSearch(HttpExchange exchange, String method) throws IOException, DatabaseException {
        if (!method.equals("GET")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = ApiUtils.parseQueryParams(query);

        List<Movie> results = movieService.searchMovies(params);
        String json = gson.toJson(results);

        ApiUtils.sendResponse(exchange, 200, json);
    }

    private String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes());
    }
}