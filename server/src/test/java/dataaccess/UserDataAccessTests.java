package dataaccess;

import exceptions.ResponseException;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserDataAccessTests {

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
    }
}