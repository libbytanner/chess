package model;

public class ResponseException extends RuntimeException {
    public final int code;

    public ResponseException(String message, int code) {
        super(message);
        this.code = code;
    }
}
