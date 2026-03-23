package at.ac.fhcampuswien.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.ApiUtils;

import java.io.*;
import java.util.*;

public class MovieController implements HttpHandler {

    private List<Movie> movies = Movie.generateDummyMovies();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // routing by endpoint
        if (path.endsWith("/getAll")) {
            handleGetAll(exchange, method);

        } else if (path.endsWith("/add")) {
            handleAdd(exchange, method);

        } else if (path.endsWith("/delete")) {
            handleDelete(exchange, method);

        } else if (path.endsWith("/update")) {
            handleUpdate(exchange, method);

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

        String json = "[";

        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);

            json += "{";
            json += "\"id\":\"" + m.getId() + "\",\n";
            json += "\"title\":\"" + m.getTitle() + "\",\n";
            json += "\"genre\":\"" + m.getGenre() + "\",\n";
            json += "\"releaseYear\":" + m.getReleaseYear();
            json += "}";

            if (i < movies.size() - 1) json += ",\n\n";
        }

        json += "]";

        ApiUtils.sendResponse(exchange, 200, json);
    }

    // add
    private void handleAdd(HttpExchange exchange, String method) throws IOException {

        if (!method.equals("POST")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = readBody(exchange);
        Movie newMovie = parseMovieWithoutId(body);

        if (newMovie == null) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            return;
        }

        for (Movie m : movies) {
            if (m.getTitle().equals(newMovie.getTitle()) &&
                    m.getGenre().equals(newMovie.getGenre()) &&
                    m.getReleaseYear() == newMovie.getReleaseYear()) {

                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Movie already exists\" }");
                return;
            }
        }

        movies.add(newMovie);
        ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");
    }

    // delete
    private void handleDelete(HttpExchange exchange, String method) throws IOException {

        if (!method.equals("DELETE")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = readBody(exchange);
        Movie toDelete = parseMovieWithoutId(body);

        if (toDelete == null) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            return;
        }

        for (Movie m : movies) {
            if (m.getTitle().equals(toDelete.getTitle()) &&
                    m.getGenre().equals(toDelete.getGenre()) &&
                    m.getReleaseYear() == toDelete.getReleaseYear()) {

                movies.remove(m);
                ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");
                return;
            }
        }

        ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
    }

    // update
    private void handleUpdate(HttpExchange exchange, String method) throws IOException {

        if (!method.equals("PUT")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        String body = readBody(exchange);

        try {
            String idStr = body.split("\"id\":\"")[1].split("\"")[0];
            UUID id = UUID.fromString(idStr);

            String title = body.split("\"title\":\"")[1].split("\"")[0];
            String genre = body.split("\"genre\":\"")[1].split("\"")[0];
            int year = Integer.parseInt(body.split("\"releaseYear\":")[1].split("}")[0]);

            for (Movie m : movies) {
                if (m.getId().equals(id)) {

                    // update fields
                    m.setTitle(title);
                    m.setGenre(genre);
                    m.setReleaseYear(year);

                    ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie updated successfully\" }");
                    return;
                }
            }

            ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");

        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
        }
    }

    // read body
    private String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes());
    }

    // parse json (manual)
    private Movie parseMovieWithoutId(String body) {
        try {
            String title = body.split("\"title\":\"")[1].split("\"")[0];
            String genre = body.split("\"genre\":\"")[1].split("\"")[0];
            int year = Integer.parseInt(body.split("\"releaseYear\":")[1].split("}")[0]);

            return new Movie(title, genre, year);

        } catch (Exception e) {
            return null;
        }
    }
}