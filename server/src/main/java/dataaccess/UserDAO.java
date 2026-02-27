package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    ArrayList<UserData> getUsers();
    UserData getUser(String username);
    void createUser(UserData user);
    void clear();
}
