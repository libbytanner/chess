package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {
    ArrayList<AuthData> getAuthTokens();
    void createAuth(AuthData auth);
    String generateToken();
    AuthData findAuth(String token);

    void clear();

}
