package dataaccess;

import model.model.AuthData;

import java.util.ArrayList;
import java.util.UUID;

public interface AuthDAO {
    ArrayList<AuthData> getAuthTokens();
    void addAuth(AuthData auth);
    default String generateToken() {
        return UUID.randomUUID().toString();
    }
    AuthData findAuth(String token);
    void deleteAuth(AuthData auth);

    void clear();
}
