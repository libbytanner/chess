package model.model;

public record LoginResult(String username, String authToken) implements Result {
}
