package at.ac.fhcampuswien;

import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        List<Movie> movies = new ArrayList<>();

        movies.add(new Movie("Inception", "Sci-Fi", 2010));
        movies.add(new Movie("Matrix", "Sci-Fi", 1999));
        movies.add(new Movie("Titanic", "Drama", 1997));

        movieService = new MovieService(movies);
    }

    @Test
    void givenMovieList_whenGetAllMovies_thenReturnAllMovies() {
        List<Movie> result = movieService.getAllMovies();

        assertEquals(3, result.size());
    }

    @Test
    void givenNewMovie_whenAddMovie_thenMovieIsAdded() {
        Movie newMovie = new Movie ("Avatar", "Fantasy", 2009);

        boolean result = movieService.addMovie(newMovie);

        assertTrue(result);
        assertEquals(4, movieService.getAllMovies().size());
    }

    @Test
    void givenExistingMovie_whenAddMovie_thenReturnFalse() {
        Movie duplicateMovie = new Movie("Inception", "Sci-Fi", 2010);

        boolean result = movieService.addMovie(duplicateMovie);

        assertFalse(result);
        assertEquals(3, movieService.getAllMovies().size());
    }

    @Test
    void givenExistingMovie_whenDeleteMovie_thenMovieIsDeleted() {
        Movie movieToDelete = new Movie("Matrix", "Sci-Fi", 1999);

        boolean result = movieService.deleteMovie(movieToDelete);

        assertTrue(result);
        assertEquals(2, movieService.getAllMovies().size());
    }

    @Test
    void givenNonExistingMovie_whenDeleteMovie_thenReturnFalse() {
        Movie movieToDelete = new Movie("Avatar", "Fantasy", 2009);

        boolean result = movieService.deleteMovie(movieToDelete);

        assertFalse(result);
        assertEquals(3, movieService.getAllMovies().size());
    }

    @Test
    void givenExistingMovieId_whenUpdateMovie_thenMovieIsUpdated() {
        Movie existingMovie = movieService.getAllMovies().get(0);

        Movie updatedMovie = new Movie("Interstellar", "Sci-Fi", 2014);
        updatedMovie.setId(existingMovie.getId());

        boolean result = movieService.updateMovie(updatedMovie);

        assertTrue(result);
        assertEquals("Interstellar", existingMovie.getTitle());
        assertEquals("Sci-Fi", existingMovie.getGenre());
        assertEquals(2014, existingMovie.getReleaseYear());
    }

    @Test
    void givenNonExistingMovieId_whenUpdateMovie_thenReturnFalse() {
        Movie updatedMovie = new Movie("Avatar", "Fantasy", 2009);

        boolean result = movieService.updateMovie(updatedMovie);

        assertFalse(result);
    }

    @Test
    void givenPartialTitle_whenSearchMovies_thenReturnMatchingMovie() {
        Map<String, String> params = new HashMap<>();
        params.put("title", "incep");

        List<Movie> result = movieService.searchMovies(params);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenLowerCaseGenreWithoutHyphen_whenSearchMovies_thenReturnMatchingMovies() {
        Map<String, String> params = new HashMap<>();
        params.put("genre", "scifi");

        List<Movie> result = movieService.searchMovies(params);

        assertEquals(2, result.size());
    }

    @Test
    void givenReleaseYear_whenSearchMovies_thenReturnMatchingMovie() {
        Map<String, String> params = new HashMap<>();
        params.put("releaseYear", "2010");

        List<Movie> result = movieService.searchMovies(params);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenTitleAndGenre_whenSearchMovies_thenReturnMatchingMovie() {
        Map<String, String> params = new HashMap<>();
        params.put("title", "mat");
        params.put("genre", "sci-fi");

        List<Movie> result = movieService.searchMovies(params);

        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getTitle());
    }
}

