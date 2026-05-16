package at.ac.fhcampuswien;

// neu: databaseexception importiert weil repository methoden diese exception deklarieren
// und die test methoden sie in ihrer signatur angeben müssen
import at.ac.fhcampuswien.exceptions.DatabaseException;
// neu: movienotfoundexception importiert weil tests prüfen ob diese exception korrekt geworfen wird
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
// neu: movierepository wird importiert damit es gemockt werden kann
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
// neu: mockito imports - mock() erstellt ein fake objekt - when() definiert was das fake objekt zurückgeben soll
// verify() prüft ob eine bestimmte methode auf dem mock aufgerufen wurde
import static org.mockito.Mockito.*;

public class MovieServiceTest {

    private MovieService movieService;

    // neu: statt einer list<movie> gibt es jetzt eine movierepository instanz als feld
    // diese wird in setup() als mock erstellt - kein echtes repository kein echter db zugriff
    private MovieRepository movieRepository;

    // neu: setup methode deklariert throws databaseexception
    // das ist nötig weil when(movierepository.findall()).thenreturn(movies) intern die methode aufruft
    // und findall() eine databaseexception in der signatur hat - java verlangt dass das deklariert wird
    // in tests ist es ok exceptions in der signatur zu haben weil junit den test einfach als failed markiert wenn eine unerwartet geworfen wird
    @BeforeEach
    void setUp() throws DatabaseException {

        // neu: mock(MovieRepository.class) erstellt ein fake movierepository objekt
        // dieses objekt hat alle methoden von movierepository aber macht standardmäßig nichts
        // so wird getestet ob movieservice korrekt mit dem repository interagiert ohne eine echte db zu brauchen
        movieRepository = mock(MovieRepository.class);

        List<Movie> movies = new ArrayList<>(Arrays.asList(
                new Movie("Inception", "Sci-Fi", 2010),
                new Movie("Matrix", "Sci-Fi", 1999),
                new Movie("Titanic", "Drama", 1997)
        ));

        // neu: when().thenreturn() sagt dem mock was er zurückgeben soll wenn findall() aufgerufen wird
        // jedes mal wenn movieservice intern movierepository.findall() aufruft bekommt er diese liste zurück
        when(movieRepository.findAll()).thenReturn(movies);

        // neu: movieservice bekommt jetzt das gemockte repository statt einer liste
        // das entspricht der geänderten konstruktor signatur von movieservice
        movieService = new MovieService(movieRepository);
    }

    // neu: throws databaseexception in der test signatur weil getAllMovies() jetzt databaseexception deklariert
    @Test
    void givenMovieList_whenGetAllMovies_thenReturnAllMovies() throws DatabaseException {
        List<Movie> result = movieService.getAllMovies();
        assertEquals(3, result.size());
    }

    // neu: throws databaseexception hinzugefügt
    // neu: verify(movierepository).add(newmovie) prüft ob movierepository.add() tatsächlich aufgerufen wurde
    // das stellt sicher dass movieservice nicht einfach die liste ignoriert sondern wirklich ans repository delegiert
    @Test
    void givenNewMovie_whenAddMovie_thenMovieIsAdded() throws DatabaseException {
        Movie newMovie = new Movie("Avatar", "Fantasy", 2009);
        boolean result = movieService.addMovie(newMovie);
        assertTrue(result);
        verify(movieRepository).add(newMovie);
    }

    // neu: throws databaseexception hinzugefügt
    @Test
    void givenExistingMovie_whenAddMovie_thenReturnFalse() throws DatabaseException {
        Movie duplicateMovie = new Movie("Inception", "Sci-Fi", 2010);
        boolean result = movieService.addMovie(duplicateMovie);
        assertFalse(result);
    }

    // neu: throws databaseexception und throws movienotfoundexception hinzugefügt
    // neu: when(movierepository.delete(movieToDelete)).thenreturn(true) simuliert einen erfolgreichen delete
    // ohne mock würde delete() nichts tun und false zurückgeben weil kein echter db aufruf passiert
    @Test
    void givenExistingMovie_whenDeleteMovie_thenMovieIsDeleted() throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie("Matrix", "Sci-Fi", 1999);
        when(movieRepository.delete(movieToDelete)).thenReturn(true);

        boolean result = movieService.deleteMovie(movieToDelete);
        assertTrue(result);
    }

    // neu: kompletter test neu geschrieben
    // neu: when().thenthrow() simuliert dass das repository eine movienotfoundexception wirft
    // assertthrows prüft ob movieservice diese exception korrekt weiterpropagiert und nicht schluckt
    @Test
    void givenNonExistingMovie_whenDeleteMovie_thenReturnFalse() throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie("Avatar", "Fantasy", 2009);
        when(movieRepository.delete(movieToDelete)).thenThrow(new MovieNotFoundException("Movie not found for deletion"));

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(movieToDelete));
    }

    // neu: throws databaseexception und throws movienotfoundexception hinzugefügt
    // neu: when(movierepository.update()).thenreturn(true) simuliert erfolgreichen update
    @Test
    void givenExistingMovieId_whenUpdateMovie_thenMovieIsUpdated() throws DatabaseException, MovieNotFoundException {
        Movie updatedMovie = new Movie("Interstellar", "Sci-Fi", 2014);
        when(movieRepository.update(updatedMovie)).thenReturn(true);

        boolean result = movieService.updateMovie(updatedMovie);
        assertTrue(result);
    }

    // neu: kompletter test neu geschrieben
    // neu: mock wirft movienotfoundexception bei update() - assertthrows prüft ob movieservice sie propagiert
    @Test
    void givenNonExistingMovieId_whenUpdateMovie_thenReturnFalse() throws DatabaseException, MovieNotFoundException {
        Movie updatedMovie = new Movie("Avatar", "Fantasy", 2009);
        when(movieRepository.update(updatedMovie)).thenThrow(new MovieNotFoundException("Movie not found for update"));

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(updatedMovie));
    }

    // neu: throws databaseexception hinzugefügt weil searchmovies() jetzt databaseexception deklariert
    @Test
    void givenPartialTitle_whenSearchMovies_thenReturnMatchingMovie() throws DatabaseException {
        Map<String, String> params = new HashMap<>();
        params.put("title", "incep");

        List<Movie> result = movieService.searchMovies(params);
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    // neu: throws databaseexception hinzugefügt
    @Test
    void givenLowerCaseGenreWithoutHyphen_whenSearchMovies_thenReturnMatchingMovies() throws DatabaseException {
        Map<String, String> params = new HashMap<>();
        params.put("genre", "scifi");

        List<Movie> result = movieService.searchMovies(params);
        assertEquals(2, result.size());
    }

    // neu: throws databaseexception hinzugefügt
    @Test
    void givenReleaseYear_whenSearchMovies_thenReturnMatchingMovie() throws DatabaseException {
        Map<String, String> params = new HashMap<>();
        params.put("releaseYear", "2010");

        List<Movie> result = movieService.searchMovies(params);
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    // neu: throws databaseexception hinzugefügt
    @Test
    void givenTitleAndGenre_whenSearchMovies_thenReturnMatchingMovie() throws DatabaseException {
        Map<String, String> params = new HashMap<>();
        params.put("title", "mat");
        params.put("genre", "sci-fi");

        List<Movie> result = movieService.searchMovies(params);
        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getTitle());
    }

    // neu: pflicht-test laut aufgabenstellung - prüft ob databaseexception korrekt propagiert wird
    // when().thenthrow() simuliert einen datenbankfehler beim delete aufruf im repository
    // assertthrows prüft ob movieservice.deletemovie() die databaseexception nicht schluckt sondern weitergibt
    // das ist wichtig weil der controller darauf angewiesen ist die exception zu empfangen um http 500 zurückzugeben
    @Test
    void should_throw_database_exception_when_deleting_movie_with_db_error() throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie("Inception", "Sci-Fi", 2010);
        when(movieRepository.delete(movieToDelete)).thenThrow(new DatabaseException("Database connection error"));

        assertThrows(DatabaseException.class, () -> movieService.deleteMovie(movieToDelete));
    }

    // neu: pflicht-test laut aufgabenstellung - prüft ob movienotfoundexception bei update korrekt propagiert wird
    // when().thenthrow() simuliert dass das repository keinen film mit der id findet (rowsaffected == 0 im sql update)
    // assertthrows prüft ob movieservice.updatemovie() die exception weitergibt statt false zurückzugeben
    @Test
    void should_throw_movie_not_found_exception_when_updating_nonexistent_movie() throws DatabaseException, MovieNotFoundException {
        Movie movieToUpdate = new Movie("Ghost Movie", "Horror", 2000);
        when(movieRepository.update(movieToUpdate)).thenThrow(new MovieNotFoundException("Movie not found for update"));

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(movieToUpdate));
    }
}