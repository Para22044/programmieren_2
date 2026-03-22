package at.ac.fhcampuswien.models;

import java.util.UUID;

public class Movie {

    private UUID id;
    private String title;
    private String genre;
    private int releaseYear;

    // Constructor
    public Movie(String title, String genre, int releaseYear) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    // Getters (REQUIRED for controller)
    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }
}