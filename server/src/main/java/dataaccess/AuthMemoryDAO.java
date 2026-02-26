package dataaccess;

import model.AuthData;

import java.util.ArrayList;
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
}
