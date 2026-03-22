package at.ac.fhcampuswien.controllers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import at.ac.fhcampuswien.models.Movie;
import java.io.*;
import java.util.*;

    public class MovieController implements HttpHandler {
        private List<Movie> movies = new ArrayList<>();
        public MovieController() {
            movies.add(new Movie("Shawshank Redemption", "Drama", 2001));
            movies.add(new Movie("Snatch", "Comedy", 1997));
            movies.add(new Movie("Deadpool", "Action", 2016));
            movies.add(new Movie("Cars", "Animation", 2006));
            movies.add(new Movie("Interstellar", "Sci-Fi", 2014));
        }



        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if (path.equals("/api/movies/getAll")) {
                handleGetAll(exchange, method);
            }

            else if (path.equals("/api/movies/add")) {
                handleAdd(exchange, method);
            }


            else if (path.equals("/api/movies/delete")) {
                handleDelete(exchange, method);
            }


            else if (path.equals("/api/movies/update")) {
                handleUpdate(exchange, method);
            }


            else {
                sendResponse(exchange, 404, "{ \"error\": \"Path not found\" }");
            }

        }



        //Get all
        private void handleGetAll(HttpExchange exchange, String method) throws IOException {


            if (!method.equals("GET")) {
                sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
                return;
            }

            String json = "[";

            for (int i = 0; i < movies.size(); i++) {
                Movie m = movies.get(i);
                json += "{";
                json += "\"id\":\"" + m.getId() + "\",";
                json += "\"title\":\"" + m.getTitle() + "\",";
                json += "\"genre\":\"" + m.getGenre() + "\",";
                json += "\"releaseYear\":" + m.getReleaseYear();
                json += "}";

                if (i < movies.size() - 1) json += ",";
            }

            json += "]";
            sendResponse(exchange, 200, json);
        }






        // add
        private void handleAdd(HttpExchange exchange, String method) throws IOException {

            if (!method.equals("POST")) {
                sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
                return;
            }

            String body = readBody(exchange);
            Movie newMovie = parseMovieWithoutId(body);

            if (newMovie == null) {
                sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                return;
            }

            for (Movie m : movies) {
                if (m.getTitle().equals(newMovie.getTitle()) &&
                        m.getGenre().equals(newMovie.getGenre()) &&
                        m.getReleaseYear() == newMovie.getReleaseYear()) {

                    sendResponse(exchange, 400, "{ \"error\": \"Movie already exists\" }");
                    return;
                }
            }

            movies.add(newMovie);
            sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");
        }




        // delete
        private void handleDelete(HttpExchange exchange, String method) throws IOException {

            if (!method.equals("DELETE")) {
                sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
                return;
            }

            String body = readBody(exchange);
            Movie toDelete = parseMovieWithoutId(body);

            if (toDelete == null) {
                sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                return;
            }

            for (Movie m : movies) {
                if (m.getTitle().equals(toDelete.getTitle()) &&
                        m.getGenre().equals(toDelete.getGenre()) &&
                        m.getReleaseYear() == toDelete.getReleaseYear()) {

                    movies.remove(m);
                    sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");
                    return;
                }
            }

            sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
        }








        // updtae
        private void handleUpdate(HttpExchange exchange, String method) throws IOException {

            if (!method.equals("PUT")) {
                sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
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
                        movies.remove(m);
                        movies.add(new Movie(title, genre, year));
                        sendResponse(exchange, 200, "{ \"message\": \"Movie updated successfully\" }");
                        return;
                    }
                }

                sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");

            } catch (Exception e) {
                sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
            }
        }








        // helper

        private String readBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            return new String(is.readAllBytes());
        }

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



        private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }