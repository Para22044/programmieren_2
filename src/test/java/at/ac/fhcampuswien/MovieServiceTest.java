package at.ac.fhcampuswien;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovieServiceTest {

    private MovieService movieService;
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() throws DatabaseException {

        movieRepository = mock(MovieRepository.class);

        List<Movie> movies = new ArrayList<>(Arrays.asList(
                new Movie("Inception", "Sci-Fi", 2010),
                new Movie("Matrix", "Sci-Fi", 1999),
                new Movie("Titanic", "Drama", 1997)
        ));

        when(movieRepository.findAll()).thenReturn(movies);

        movieService = new MovieService(movieRepository);
    }

    @Test
    void givenMovieList_whenGetAllMovies_thenReturnAllMovies() throws DatabaseException {
        List<Movie> result = movieService.getAllMovies();
        assertEquals(3, result.size());
    }

    @Test
    void givenNewMovie_whenAddMovie_thenMovieIsAdded() throws DatabaseException {
        Movie newMovie = new Movie("Avatar", "Fantasy", 2009);
        boolean result = movieService.addMovie(newMovie);
        assertTrue(result);
        verify(movieRepository).add(newMovie);
    }

    @Test
    void givenExistingMovie_whenAddMovie_thenReturnFalse() throws DatabaseException {
        Movie duplicateMovie = new Movie("Inception", "Sci-Fi", 2010);
        boolean result = movieService.addMovie(duplicateMovie);
        assertFalse(result);
    }

    @Test
    void givenExistingMovie_whenDeleteMovie_thenMovieIsDeleted() throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie("Matrix", "Sci-Fi", 1999);
        when(movieRepository.delete(movieToDelete)).thenReturn(true);

        boolean result = movieService.deleteMovie(movieToDelete);
        assertTrue(result);
    }

    @Test
    void givenNonExistingMovie_whenDeleteMovie_thenReturnFalse() throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie("Avatar", "Fantasy", 2009);
        when(movieRepository.delete(movieToDelete)).thenThrow(new MovieNotFoundException("Movie not found for deletion"));

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(movieToDelete));
    }

    @Test
    void givenExistingMovieId_whenUpdateMovie_thenMovieIsUpdated() throws DatabaseException, MovieNotFoundException {
        Movie updatedMovie = new Movie("Interstellar", "Sci-Fi", 2014);
        when(movieRepository.update(updatedMovie)).thenReturn(true);

        boolean result = movieService.updateMovie(updatedMovie);
        assertTrue(result);
    }

    @Test
    void givenNonExistingMovieId_whenUpdateMovie_thenReturnFalse() throws DatabaseException, MovieNotFoundException {
        Movie updatedMovie = new Movie("Avatar", "Fantasy", 2009);
        when(movieRepository.update(updatedMovie)).thenThrow(new MovieNotFoundException("Movie not found for update"));

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(updatedMovie));
    }

    @Test
    void givenPartialTitle_whenSearchMovies_thenReturnMatchingMovie() throws DatabaseException {
        Map<String, String> params = new HashMap<>();
        params.put("title", "incep");

        List<Movie> result = movieService.searchMovies(params);
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenLowerCaseGenreWithoutHyphen_whenSearchMovies_thenReturnMatchingMovies() throws DatabaseException {
        Map<String, String> params = new HashMap<>();
        params.put("genre", "scifi");

        List<Movie> result = movieService.searchMovies(params);
        assertEquals(2, result.size());
    }

    @Test
    void givenReleaseYear_whenSearchMovies_thenReturnMatchingMovie() throws DatabaseException {
        Map<String, String> params = new HashMap<>();
        params.put("releaseYear", "2010");

        List<Movie> result = movieService.searchMovies(params);
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenTitleAndGenre_whenSearchMovies_thenReturnMatchingMovie() throws DatabaseException {
        Map<String, String> params = new HashMap<>();
        params.put("title", "mat");
        params.put("genre", "sci-fi");

        List<Movie> result = movieService.searchMovies(params);
        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getTitle());
    }

    @Test
    void should_throw_database_exception_when_deleting_movie_with_db_error() throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie("Inception", "Sci-Fi", 2010);
        when(movieRepository.delete(movieToDelete)).thenThrow(new DatabaseException("Database connection error"));

        assertThrows(DatabaseException.class, () -> movieService.deleteMovie(movieToDelete));
    }

    @Test
    void should_throw_movie_not_found_exception_when_updating_nonexistent_movie() throws DatabaseException, MovieNotFoundException {
        Movie movieToUpdate = new Movie("Ghost Movie", "Horror", 2000);
        when(movieRepository.update(movieToUpdate)).thenThrow(new MovieNotFoundException("Movie not found for update"));

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(movieToUpdate));
    }
}