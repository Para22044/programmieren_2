package at.ac.fhcampuswien.strategies;

import at.ac.fhcampuswien.models.Movie;

public interface MovieFilterStrategy {

    //Jede Filterstrategie entscheidet selbst, ob ein Film passt
    boolean matches(Movie movie);
}
