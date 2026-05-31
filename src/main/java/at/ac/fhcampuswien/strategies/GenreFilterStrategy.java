package at.ac.fhcampuswien.strategies;

import at.ac.fhcampuswien.models.Movie;

public class GenreFilterStrategy implements MovieFilterStrategy {

    private final String genre;

    public GenreFilterStrategy(String genre) {
        this.genre = genre.toLowerCase().replace("-", "");
    }

    @Override
    public boolean matches(Movie movie) {
        String movieGenre = movie.getGenre().toLowerCase().replace("-", "");
        return movieGenre.contains(genre);
    }
}
/*
Das ist "Behavioral Pattern Strategy" um die Filme nach Genre zu filtern.
*/
