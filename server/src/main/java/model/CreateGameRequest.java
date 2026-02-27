package model;

public record CreateGameRequest(String authToken, String gameName) implements Request {}
