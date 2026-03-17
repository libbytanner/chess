package client;

import model.UserData;

import java.net.http.HttpClient;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public UserData register(String username, String password, String email) {return null;}

    public UserData login(String username, String password) {return null;}

    public void logout(String username, String password) {}

}
