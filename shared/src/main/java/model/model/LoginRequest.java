package model.model;

public record LoginRequest(String username, String password) implements Request {
}
