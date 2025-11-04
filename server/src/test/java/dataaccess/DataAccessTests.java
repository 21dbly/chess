package dataaccess;

import exceptions.*;
import model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTests {

    final AuthData user1Auth = new AuthData("12345", "userOne");
    final AuthData authNoUser = new AuthData("12345", null);
    final AuthData authNoToken = new AuthData(null, "userOne");
    final LoginRequest user1Login = new LoginRequest("userOne", "pass111");
    final LoginRequest wrongPasswordLogin = new LoginRequest("userOne", "wrongPass");
    final UserData testSameUsername = new UserData("userOne", "differentPass", "different@mail.com");
    final UserData user2 = new UserData("userTwo", "pass222", "two@mail.com");
    final LoginRequest user2Login = new LoginRequest("userTwo", "pass222");
    final String game1Name = "game1!";
    final String game2Name = "game2?";

    private AuthDAO getAuthDAO(Class<? extends AuthDAO> authDAOClass) throws DataAccessException {
        AuthDAO authDAO;
        if (authDAOClass.equals(SQLAuthDAO.class)) {
            authDAO = new SQLAuthDAO();
        } else {
            authDAO = new MemoryAuthDAO();
        }
        authDAO.clear();
        return authDAO;
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createAuthValid(Class<? extends AuthDAO> authDAOclass) throws ResponseException {
        AuthDAO authDAO = getAuthDAO(authDAOclass);
        assertDoesNotThrow(() -> authDAO.createAuth(user1Auth));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createAuthNoToken(Class<? extends AuthDAO> authDAOclass) throws ResponseException {
        AuthDAO authDAO = getAuthDAO(authDAOclass);
        assertThrows(DataAccessException.class, () ->
                authDAO.createAuth(authNoToken));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createAuthNoUsername(Class<? extends AuthDAO> authDAOclass) throws ResponseException {
        AuthDAO authDAO = getAuthDAO(authDAOclass);
        assertThrows(DataAccessException.class, () ->
                authDAO.createAuth(authNoUser));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void getAuthValid(Class<? extends AuthDAO> authDAOclass) throws ResponseException {
        AuthDAO authDAO = getAuthDAO(authDAOclass);
        authDAO.createAuth(user1Auth);
        AuthData returnedAuthData = authDAO.getAuth(user1Auth.authToken());
        assertEquals(user1Auth, returnedAuthData);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void getAuthEmpty(Class<? extends AuthDAO> authDAOclass) throws ResponseException {
        AuthDAO authDAO = getAuthDAO(authDAOclass);
        authDAO.createAuth(user1Auth);
        AuthData returnedAuthData = authDAO.getAuth("thisAuthTokenDoesNotExist");
        assertNull(returnedAuthData);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void deleteAuthValid(Class<? extends AuthDAO> authDAOclass) throws ResponseException {
        AuthDAO authDAO = getAuthDAO(authDAOclass);
        authDAO.createAuth(user1Auth);
        authDAO.deleteAuth(user1Auth.authToken());
        AuthData returnedAuthData = authDAO.getAuth(user1Auth.authToken());
        assertNull(returnedAuthData);
    }


}