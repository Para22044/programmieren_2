package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

import java.util.List;

public interface MovieRepository {

    //Speichert einen Film in einer Datenquelle
    void add(Movie movie) throws DatabaseException;

    //Holt alle Filme aus der Datenquelle
    List<Movie> findAll() throws DatabaseException;

    //Löscht einen Film aus der Datenquelle
    boolean delete(Movie movie) throws DatabaseException, MovieNotFoundException;

    //Aktualisiert einen Film in der Datenquelle
    boolean update(Movie movie) throws DatabaseException, MovieNotFoundException;
}
/*
Für DIP und ISP, da "MovieService" nicht mehr von einer konkreten Datenbankklasse abhängt, sondern nur vom kleinen
Repository-Interface.
 */
