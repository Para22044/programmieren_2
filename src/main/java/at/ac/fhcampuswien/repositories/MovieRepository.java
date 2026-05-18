package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.database.DatabaseUtil;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
// Talks directly with the Database
public class MovieRepository {

    public void add(Movie movie) throws DatabaseException {
        String sql = "INSERT INTO movies (id, title, genre, release_year) VALUES (?, ?, ?, ?)";     // Insert Movie

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Applies values reliably, because of Protection against SQL Injections
            //replaced statement.setObject(1, movie.getId()); with
            if (movie.getId() == null) {
                movie.setId(UUID.randomUUID());
            }
            statement.setObject(1, movie.getId());
            statement.setString(2, movie.getTitle());
            statement.setString(3, movie.getGenre());
            statement.setInt(4, movie.getReleaseYear());

            statement.executeUpdate();      // Executes SQL

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Could not add movie to database", e);
        }
    }
    // Get/Find all the movies
    public List<Movie> findAll() throws DatabaseException {
        String sql = "SELECT * FROM movies";
        List<Movie> movies = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            // Moves through the results line by line
            while (resultSet.next()) {
                Movie movie = new Movie(        // Creates again the Java Object
                        resultSet.getString("title"),
                        resultSet.getString("genre"),
                        resultSet.getInt("release_year")
                );

                movie.setId((UUID) resultSet.getObject("id"));
                movies.add(movie);
            }
            return movies;

        } catch (SQLException e) {
            throw new DatabaseException("Could not read movies from database", e);
        }
    }
    // Delete movies
    public boolean delete(Movie movie) throws DatabaseException, MovieNotFoundException {
        String sql = "DELETE FROM movies WHERE title = ? AND genre = ? AND release_year = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getGenre());
            statement.setInt(3, movie.getReleaseYear());




















            int rowsAffected = statement.executeUpdate();
            // Checks if anything was deleted
            if (rowsAffected == 0) {
                throw new MovieNotFoundException("Movie not found for deletion");       // If nothing has been deleted "..."
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Could not delete movie from database", e);
        }
    }
    // Update movies
    public boolean update(Movie movie) throws DatabaseException, MovieNotFoundException {
        String sql = "UPDATE movies SET title = ?, genre = ?, release_year = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getGenre());
            statement.setInt(3, movie.getReleaseYear());
            statement.setObject(4, movie.getId());

            int rowsAffected = statement.executeUpdate();
            // If Movie doesn't exist -> Movie not found
            if (rowsAffected == 0) {
                throw new MovieNotFoundException("Movie not found for update");
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Could not update movie in database", e);
        }
    }
}
