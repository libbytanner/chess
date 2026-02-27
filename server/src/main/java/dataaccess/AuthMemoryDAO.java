package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class AuthMemoryDAO implements AuthDAO{
    ArrayList<AuthData> authTokens = new ArrayList<>();

    public ArrayList<AuthData> getAuthTokens() {
        return authTokens;
    }

    public void createAuth(AuthData auth) {
        authTokens.add(auth);
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData findAuth(String token) {
        for (AuthData auth : authTokens) {
            if (Objects.equals(auth.authToken(), token)) {
                return auth;
            }
        }
        return null;
    }

    public void clear() {
        authTokens = new ArrayList<>();
    }
}
