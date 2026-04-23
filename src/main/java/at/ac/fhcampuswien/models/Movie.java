package at.ac.fhcampuswien.models;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

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
    // Getters & Setters
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Override
    public String toString() {
        return "Movie{id=" + id + ", title='" + title + "', genre='" + genre + "', releaseYear=" + releaseYear + "}";
    }

    public static List<Movie> generateDummyMovies() {
        List<Movie> movies = new ArrayList<>();
        String[] titles = {
                "Shawshank Redemption", "Snatch", "Deadpool", "Interstellar",
                "Joker", "Rocky", "Alien", "Matrix", "Dune", "Up", "Frozen",
                "Shrek", "Gladiator", "Inception", "Avatar", "Cars", "It"
        };
        String[] genres = {
                "Action", "Drama", "Comedy", "Horror", "Thriller", "Sci-Fi", "Fantasy"
        };
        Random random = new Random();
        for (int i = 0; i < 17; i++) {
            String title = titles[i];
            String genre = genres[random.nextInt(genres.length)];
            int year = 1980 + random.nextInt(45);
            movies.add(new Movie(title, genre, year));
        }
        return movies;
    }
}