package client;

import com.google.gson.Gson;
import model.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import java.net.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public RegisterResult register(RegisterRequest regRequest) {
        var request = buildRequest("POST", "/user", regRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) {
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(LogoutRequest logoutRequest) {
        var request = buildRequest("DELETE", "/session", null, logoutRequest.authToken());
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Request body, String auth) {
        var request = HttpRequest.newBuilder()
            .uri(URI.create(serverUrl + path))
            .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (auth != null) {
            request.setHeader("authorization", auth);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Request request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) {
        var status = response.statusCode();
        if (status != 200) {
            var body = response.body();
            throw new ResponseException(body, status);
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    public void clear() {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }
}
