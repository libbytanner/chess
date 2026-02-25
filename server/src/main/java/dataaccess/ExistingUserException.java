package dataaccess;

public class ExistingUserException extends DataAccessException {
    public ExistingUserException(String message) {
        super(message);
    }
}
