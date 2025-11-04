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
    final UserData user2 = new UserData("userTwo", "pass222", "two@mail.com");

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

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getUserValid(Class<? extends UserDAO> userDAOclass) throws ResponseException {
        UserDAO userDAO = getUserDAO(userDAOclass);
        userDAO.createUser(user1);
        assertEquals(user1, userDAO.getUser(user1.username()));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getUserEmpty(Class<? extends UserDAO> userDAOclass) throws ResponseException {
        UserDAO userDAO = getUserDAO(userDAOclass);
        userDAO.createUser(user1);
        assertNull(userDAO.getUser("NotARealUsername"));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void clearUsersValid(Class<? extends UserDAO> userDAOclass) throws ResponseException {
        UserDAO userDAO = getUserDAO(userDAOclass);
        userDAO.createUser(user1);
        userDAO.createUser(user2);
        userDAO.clear();
        assertNull(userDAO.getUser(user1.username()));
        assertNull(userDAO.getUser(user2.username()));
    }
}