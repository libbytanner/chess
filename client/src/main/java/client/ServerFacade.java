package client;

import model.UserData;

import java.net.http.HttpClient;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public UserData register(String username, String password, String email) {}

    public UserData login(String username, String password) {}

    public void logout(String username, String password) {}

}
