package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.strategies.MovieSearchService;

import java.util.List;
import java.util.Map;

public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieSearchService movieSearchService;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        this.movieSearchService = new MovieSearchService();
    }

    public List<Movie> getAllMovies() throws DatabaseException {
        return movieRepository.findAll();
    }

    public boolean addMovie(Movie newMovie) throws DatabaseException {
        List<Movie> existing = movieRepository.findAll();

        boolean exists = existing.stream().anyMatch(m ->
                        m.getTitle().equals(newMovie.getTitle()) &&
                        m.getGenre().equals(newMovie.getGenre()) &&
                        m.getReleaseYear() == newMovie.getReleaseYear()
        );

        if (exists) {
            return false;
        }

        movieRepository.add(newMovie);
        return true;
    }

    public boolean deleteMovie(Movie toDelete) throws DatabaseException, MovieNotFoundException {
        return movieRepository.delete(toDelete);
    }

    public boolean updateMovie(Movie updatedMovie) throws DatabaseException, MovieNotFoundException {
        return movieRepository.update(updatedMovie);
    }

    public List<Movie> searchMovies(Map<String, String> params) throws DatabaseException {
        List<Movie> allMovies = movieRepository.findAll();
        return movieSearchService.filterMovies(allMovies, params);
    }
}
