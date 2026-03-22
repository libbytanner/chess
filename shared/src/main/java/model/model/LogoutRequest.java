package model.model;

public record LogoutRequest(String authToken) implements Request {
}
