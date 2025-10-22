package service;

import dataaccess.*;
import model.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChessServiceTests {
    static final ChessService service = new ChessService(
            new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO()
    );

    final UserData user1 = new UserData("userOne", "pass123", "one@mail.com");
    final LoginRequest user1Login = new LoginRequest("userOne", "pass123");
    final LoginRequest wrongPasswordLogin = new LoginRequest("userOne", "wrongPass");
    final UserData testSameUsername = new UserData("userOne", "differentPass", "different@mail.com");

    @Test
    void registerValid() throws RegistrationException, DataAccessException {
        service.clear();
        service.register(user1);
        AuthData authData = service.login(user1Login);
        assertSame(authData.username(), user1.username());
    }

    @Test
    void registerExisting() throws RegistrationException, DataAccessException {
        service.clear();
        service.register(user1);
        assertThrows(RegistrationException.class, () ->
                service.register(testSameUsername));
    }
}
