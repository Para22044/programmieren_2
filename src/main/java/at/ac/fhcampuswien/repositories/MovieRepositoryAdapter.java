package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

import java.util.List;

public class MovieRepositoryAdapter implements MovieRepository {

    //Der Adapter enthält die echte H2-Datenbankklasse
    private final H2MovieRepository h2MovieRepository;

    public MovieRepositoryAdapter(H2MovieRepository h2MovieRepository) {
        this.h2MovieRepository = h2MovieRepository;
    }

    @Override
    public void add(Movie movie) throws DatabaseException {
        h2MovieRepository.add(movie);
    }

    @Override
    public List<Movie> findAll() throws DatabaseException {
        return h2MovieRepository.findAll();
    }

    @Override
    public boolean delete(Movie movie) throws DatabaseException, MovieNotFoundException {
        return h2MovieRepository.delete(movie);
    }

    @Override
    public boolean update(Movie movie) throws DatabaseException, MovieNotFoundException {
        return h2MovieRepository.update(movie);
    }
}
/*
Das ist ein "Structural Pattern", weil der Adapter die konkrete H2-Klasse an das allgemeine
MovieRepository-Interface anpasst.
*/
