package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;

public class SQLAuthDAO implements AuthDAO {
    private final String createStatement = """
            CREATE TABLE IF NOT EXISTS authData (
                `authToken` varchar(36) NOT NULL,
                `username` varchar(256) NOT NULL,
                PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """;
    // TODO: verify elsewhere in code that username never exceeds 256 chars

    @Override
    public void clear() throws DataAccessException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        throw new RuntimeException("Not Implemented");
    }
}
