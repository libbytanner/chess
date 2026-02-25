package model;

import org.eclipse.jetty.server.Request;

public record RegisterRequest(String username, String password, String email) {}
