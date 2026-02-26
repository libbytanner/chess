package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class UserMemoryDAO {
    ArrayList<UserData> users = new ArrayList<>();

    public ArrayList<UserData> getUsers() {
        return users;
    }

    public UserData getUser(String username) {
        for (UserData user : users) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }

    public void createUser(UserData user) {
        users.add(user);
    }
}
