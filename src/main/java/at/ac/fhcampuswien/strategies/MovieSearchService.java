package at.ac.fhcampuswien.strategies;

import at.ac.fhcampuswien.models.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MovieSearchService {

    public List<Movie> filterMovies(List<Movie> movies, Map<String, String> params) {

        //Liste aller aktiven Filterstrategien
        List<MovieFilterStrategy> strategies = new ArrayList<>();

        if (params.containsKey("title")) {
            strategies.add(new TitleFilterStrategy(params.get("title")));
        }

        if (params.containsKey("genre")) {
            strategies.add(new GenreFilterStrategy(params.get("genre")));
        }

        if (params.containsKey("releaseYear")) {
            strategies.add(new ReleaseYearFilterStrategy(params.get("releaseYear")));
        }

        //Ein Film passt nur, wenn alle Strategien "true" liefern
        return movies.stream()
                .filter(movie -> strategies.stream()
                        .allMatch(strategy -> strategy.matches(movie)))
                .collect(Collectors.toList());
    }
}
/*
"Behavioral Pattern Strategy" - jeder Suchfilter ist eine eigene Strategie.
Falls später ein Director-Filter kommt, muss man nur eine neue Klasse hinzufügen und nicht
die alte Suchlogik komplett ändern.
 */
