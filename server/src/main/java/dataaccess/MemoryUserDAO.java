package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (!userData.isComplete()) {
            throw new DataAccessException("must provide username, password, and email");
        }
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }
}
