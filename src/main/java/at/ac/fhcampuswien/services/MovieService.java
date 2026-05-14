package at.ac.fhcampuswien.services;

// neu: movierepository wird importiert weil der service nicht mehr mit einer in-memory liste arbeitet sondern direkt mit der datenbank über das repository
import at.ac.fhcampuswien.exceptions.DatabaseException;
// neu: movienotfoundexception wird importiert weil methoden wie delete und update diese exception vom repository weiterwerfen können
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
// neu: movierepository import - das ist die klasse die alle sql operationen enthält
import at.ac.fhcampuswien.repositories.MovieRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MovieService {

    // neu: statt einer list<movie> wird jetzt eine movierepository instanz gehalten
    // der service selbst speichert keine movies mehr - er fragt immer die datenbank via repository
    private final MovieRepository movieRepository;

    // neu: konstruktor nimmt jetzt ein movierepository objekt statt einer list<movie>
    // das ist dependency injection - von außen (z.b. moviecontroller oder tests) wird das repository reingegeben
    // dadurch kann man in tests ein gemocktes repository reingeben ohne echte db verbindung
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // neu: throws DatabaseException hinzugefügt weil movierepository.findall() eine databaseexception werfen kann
    // wenn die db verbindung fehlschlägt oder der sql befehl nicht ausgeführt werden kann
    // die exception wird nicht hier gecatcht sondern weiterpropagiert zum controller
    public List<Movie> getAllMovies() throws DatabaseException {
        return movieRepository.findAll();
    }

    // neu: throws DatabaseException hinzugefügt
    // neu: statt movies.stream() auf einer lokalen liste wird jetzt movierepository.findall() aufgerufen
    // um zu checken ob ein film schon existiert - danach movierepository.add() statt movies.add()
    public boolean addMovie(Movie newMovie) throws DatabaseException {
        // neu: findall() holt alle filme aus der datenbank für den duplikat-check
        List<Movie> existing = movieRepository.findAll();
        boolean exists = existing.stream().anyMatch(m ->
                m.getTitle().equals(newMovie.getTitle()) &&
                        m.getGenre().equals(newMovie.getGenre()) &&
                        m.getReleaseYear() == newMovie.getReleaseYear()
        );
        if (exists) {
            return false;
        }

        // neu: movierepository.add() führt den sql insert befehl aus statt movies.add() auf einer liste
        movieRepository.add(newMovie);
        return true;
    }

    // neu: komplette methode refactored
    // throws DatabaseException weil die db verbindung fehlschlagen kann
    // throws MovieNotFoundException weil das repository diese exception wirft wenn kein film mit den kriterien gefunden wurde (rowsaffected == 0)
    // statt removeif auf einer liste wird jetzt movierepository.delete() aufgerufen der den sql delete befehl ausführt
    // rückgabewert boolean kommt direkt vom repository
    public boolean deleteMovie(Movie toDelete) throws DatabaseException, MovieNotFoundException {
        return movieRepository.delete(toDelete);
    }

    // neu: komplette methode refactored
    // throws DatabaseException und throws MovieNotFoundException aus dem gleichen grund wie bei deletemovie
    // statt in einer liste nach id zu suchen und felder manuell zu setzen wird jetzt movierepository.update() aufgerufen
    // das repository führt den sql update befehl aus und wirft movienotfoundexception wenn keine zeile betroffen war
    public boolean updateMovie(Movie updatedMovie) throws DatabaseException, MovieNotFoundException {
        return movieRepository.update(updatedMovie);
    }

    // neu: throws DatabaseException hinzugefügt weil findall() eine databaseexception werfen kann
    // die logik selbst ist identisch - alle filme werden aus der db geholt und dann in-memory gefiltert
    public List<Movie> searchMovies(Map<String, String> params) throws DatabaseException {
        // neu: movierepository.findall() statt direktem zugriff auf lokale liste
        return movieRepository.findAll().stream()
                .filter(m -> {
                    boolean matches = true;

                    if (params.containsKey("title")) {
                        matches = matches && m.getTitle().toLowerCase().contains(params.get("title").toLowerCase());
                    }

                    if (params.containsKey("genre")) {
                        String searchGenre = params.get("genre").toLowerCase().replace("-", "");
                        String movieGenre = m.getGenre().toLowerCase().replace("-", "");
                        matches = matches && movieGenre.contains(searchGenre);
                    }

                    if (params.containsKey("releaseYear")) {
                        matches = matches && String.valueOf(m.getReleaseYear()).equals(params.get("releaseYear"));
                    }

                    return matches;
                })
                .collect(Collectors.toList());
    }
}