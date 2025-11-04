package dataaccess;

import exceptions.ResponseException;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserDataAccessTests {

    final UserData user1 = new UserData("userOne", "pass111", "one@mail.com");
    final UserData userNoName = new UserData(null, "pass111", "one@mail.com");
    final UserData userNoPass = new UserData("userOne", null, "one@mail.com");
    final UserData userNoEmail = new UserData("userOne", "pass111", null);

    private UserDAO getUserDAO(Class<? extends UserDAO> userDAOClass) throws DataAccessException {
        UserDAO userDAO;
        if (userDAOClass.equals(SQLUserDAO.class)) {
            userDAO = new SQLUserDAO();
        } else {
            userDAO = new MemoryUserDAO();
        }
        userDAO.clear();
        return userDAO;
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void createUserValid(Class<? extends UserDAO> userDAOclass) throws ResponseException {
        UserDAO userDAO = getUserDAO(userDAOclass);
        assertDoesNotThrow(() ->
                userDAO.createUser(user1));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void createUserNullValues(Class<? extends UserDAO> userDAOclass) throws ResponseException {
        UserDAO userDAO = getUserDAO(userDAOclass);
        assertThrows(DataAccessException.class, () ->
                userDAO.createUser(userNoName));
        assertThrows(DataAccessException.class, () ->
                userDAO.createUser(userNoPass));
        assertThrows(DataAccessException.class, () ->
                userDAO.createUser(userNoEmail));
    }
}