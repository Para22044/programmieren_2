package at.ac.fhcampuswien.factories;

import at.ac.fhcampuswien.models.Movie;

public class MovieFactory {

    //Erstellt ein Movie-Objekt zentral an einer Stelle
    public Movie createMovie(String title, String genre, int releaseYear) {
        return new Movie(title, genre, releaseYear);
    }
}
/*
Das ist ein "Creational Pattern", weil die Objekterstellung aus dem Model ausgelagert wird.
"Movie" ist dadurch nur noch Datenklasse.
*/
