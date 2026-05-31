package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
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
    /*
    Neu: Der Controller erzeugt den Service nicht mehr selbst. Dadurch ist die Architektur sauberer
    und folgt "Dependency Injection"
    */
    private final MovieService movieService;
    private final Gson gson = new Gson();

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

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

        } catch (DatabaseException e) {
            ApiUtils.sendResponse(exchange, 500, "{ \"error\": \"" + e.getMessage() + "\" }");

        } catch (MovieNotFoundException e) {
            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"" + e.getMessage() + "\" }");

        } catch (JsonSyntaxException e) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Malformed JSON\" }");

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

        movieService.deleteMovie(toDelete);
        ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");
    }

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
