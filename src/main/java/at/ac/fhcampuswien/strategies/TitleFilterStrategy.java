package at.ac.fhcampuswien.strategies;

import at.ac.fhcampuswien.models.Movie;

public class TitleFilterStrategy implements MovieFilterStrategy {

    private final String title;

    public TitleFilterStrategy(String title) {
        this.title = title.toLowerCase();
    }

    @Override
    public boolean matches(Movie movie) {
        return movie.getTitle().toLowerCase().contains(title);
    }
}
/*
Das ist "Behavioral Pattern Strategy" um die Filme nach Titel zu filtern.
 */