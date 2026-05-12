package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MovieService {
    // this holds our list of movies
    private List<Movie> movies;

    // the constructor takes the list of movies from the outside
    //dependency injection, it helps pass whatever list
    public MovieService(List<Movie> movies) {
        this.movies = movies;
    }

    // returns all movies
    public List<Movie> getAllMovies() {
        return movies;
    }

    // adds a movie but checks if it exists first using streams instead of the old for loop
    public boolean addMovie(Movie newMovie) {
        // the stream goes through the list and the lambda expression checks if any movie matches
        boolean exists = movies.stream().anyMatch(m ->
                m.getTitle().equals(newMovie.getTitle()) &&
                        m.getGenre().equals(newMovie.getGenre()) &&
                        m.getReleaseYear() == newMovie.getReleaseYear()
        );
        if (exists) {
            return false;
        }

        movies.add(newMovie);
        return true;
    }

    // deletes a movie using the built-in remove if function which is very fast and clean
    public boolean deleteMovie(Movie toDelete) {
        // remove if loops through the list for us and removes anything that matches our lambda conditions
        return movies.removeIf(m ->
                m.getTitle().equals(toDelete.getTitle()) &&
                        m.getGenre().equals(toDelete.getGenre()) &&
                        m.getReleaseYear() == toDelete.getReleaseYear()
        );
    }

    // updates an existing movie by finding it first with a stream
    public boolean updateMovie(Movie updatedMovie) {
        Movie existingMovie = movies.stream()
                .filter(m -> m.getId().equals(updatedMovie.getId()))
                .findFirst()
                .orElse(null);

        if (existingMovie != null) {
            existingMovie.setTitle(updatedMovie.getTitle());
            existingMovie.setGenre(updatedMovie.getGenre());
            existingMovie.setReleaseYear(updatedMovie.getReleaseYear());
            return true;
        }

        return false;
    }

    // this is the new method that filters movies based on our search terms
    public List<Movie> searchMovies(Map<String, String> params) {
        return movies.stream()
                .filter(m -> {
                    boolean matches = true;

                    // if they searched for a title we make both strings lower case and check if one is inside the other
                    if (params.containsKey("title")) {
                        matches = matches && m.getTitle().toLowerCase().contains(params.get("title").toLowerCase());
                    }

                    // we do the same exact thing for the genre
                    if (params.containsKey("genre")) {
                        // we remove the hyphen just in case they search scifi instead of sci-fi
                        String searchGenre = params.get("genre").toLowerCase().replace("-", "");
                        String movieGenre = m.getGenre().toLowerCase().replace("-", "");
                        matches = matches && movieGenre.contains(searchGenre);
                    }

                    // for the year we just change the number to a string and see if they equal
                    if (params.containsKey("releaseYear")) {
                        matches = matches && String.valueOf(m.getReleaseYear()).equals(params.get("releaseYear"));
                    }

                    return matches;
                })
                // finally we gather all the matching ones back into a list
                .collect(Collectors.toList());
    }}
