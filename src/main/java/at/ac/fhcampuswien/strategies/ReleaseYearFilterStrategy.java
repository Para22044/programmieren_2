package at.ac.fhcampuswien.strategies;

import at.ac.fhcampuswien.models.Movie;

public class ReleaseYearFilterStrategy implements MovieFilterStrategy {

    private final String releaseYear;

    public ReleaseYearFilterStrategy(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Override
    public boolean matches(Movie movie) {
        return String.valueOf(movie.getReleaseYear()).equals(releaseYear);
    }
}
/*
Das ist "Behavioral Pattern Strategy" um die Filme nach Erscheinungsdatum zu filtern.
*/
