package dataaccess;

import dataaccess.memory.MemoryAuthDAO;
import dataaccess.sql.SQLAuthDAO;
import exceptions.*;
import model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTests {

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
    void testTest(Class<? extends AuthDAO> authDAOclass) throws ResponseException {
        AuthDAO authDAO = getAuthDAO(authDAOclass);
    }
}