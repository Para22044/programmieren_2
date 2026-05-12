package at.ac.fhcampuswien.exceptions;
// If movie doesn't exist
public class MovieNotFoundException extends Exception {

    public MovieNotFoundException(String message) {
        super(message);
    }
}
