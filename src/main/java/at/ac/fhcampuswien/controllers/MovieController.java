package at.ac.fhcampuswien.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.ApiUtils;

import java.io.*;
import java.util.*;

//ue2:
// importing gson to convert json to java objects
// and importing our new service layer
import com.google.gson.Gson;
import at.ac.fhcampuswien.services.MovieService;

public class MovieController implements HttpHandler {

    //ue2:
    // replacing the raw list with our service
    // injecting the dummy movies list straight into the service constructor
    // we also set up gson here so we can reuse it
    private MovieService movieService = new MovieService(Movie.generateDummyMovies());
    private Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (path.endsWith("/getAll")) {
            handleGetAll(exchange, method);
        } else if (path.endsWith("/add")) {
            handleAdd(exchange, method);
        } else if (path.endsWith("/delete")) {
            handleDelete(exchange, method);
        } else if (path.endsWith("/update")) {
            handleUpdate(exchange, method);
            //ue2:
            // adding the new route so the server calls our new search function when asked
        } else if (path.endsWith("/search")) {
            handleSearch(exchange, method);
        } else {
            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Path not found\" }");
        }
    }

    // get all
    private void handleGetAll(HttpExchange exchange, String method) throws IOException {

        if (!method.equals("GET")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        //ue2:
        // we ask the service for all movies then use gson to turn that list into text automatically
        List<Movie> allMovies = movieService.getAllMovies();
        String json = gson.toJson(allMovies);

        ApiUtils.sendResponse(exchange, 200, json);
    }

    // add
    private void handleAdd(HttpExchange exchange, String method) throws IOException {

        if (!method.equals("POST")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = readBody(exchange);
        //ue2:
        // gson turns the raw text into a beautiful movie object without us splitting anything manually
        Movie newMovie = gson.fromJson(body, Movie.class);

        if (newMovie == null) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            return;
        }

        //ue2:
        // the service does the work and gives us a simple yes or no
        boolean success = movieService.addMovie(newMovie);
        if (!success) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Movie already exists\" }");
            return;
        }

        ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");
    }

    // delete
    private void handleDelete(HttpExchange exchange, String method) throws IOException {

        if (!method.equals("DELETE")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = readBody(exchange);
        //ue2:
        // parsing with gson again
        Movie toDelete = gson.fromJson(body, Movie.class);

        if (toDelete == null) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            return;
        }

        //ue2:
        // we let the service find and delete it
        boolean success = movieService.deleteMovie(toDelete);
        if (success) {
            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");
        } else {
            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
        }
    }

    // update
    private void handleUpdate(HttpExchange exchange, String method) throws IOException {

        if (!method.equals("PUT")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = readBody(exchange);
        try {
            //ue2:
            // gson parses everything including the id automatically so we just give the object to the service
            Movie updatedMovie = gson.fromJson(body, Movie.class);
            boolean success = movieService.updateMovie(updatedMovie);

            if (success) {
                ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie updated successfully\" }");
            } else {
                ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
            }

        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
        }
    }

    //ue2:
    // our brand new search handler gets the query from the link
    // makes it a map with our utility tool gives it to the service and sends the result back as json
    private void handleSearch(HttpExchange exchange, String method) throws IOException {
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

    // read body
    private String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes());
    }

    //ue2:
    // the old parse movie without id was removed because gson does it
}