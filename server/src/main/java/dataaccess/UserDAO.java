package dataaccess;

import model.model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    ArrayList<UserData> getUsers();
    UserData getUser(String username);
    void addUser(UserData user);
    void clear();
}
