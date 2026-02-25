package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.UUID;

public class AuthMemoryDAO {
    ArrayList<AuthData> authTokens = new ArrayList<>();
    public void createAuth(AuthData auth) {
        authTokens.add(auth);
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
