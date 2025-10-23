package service;

import dataaccess.*;
import exceptions.ResponseException;
import model.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChessServiceTests {
    static final ChessService service = new ChessService(
            new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO()
    );

    final UserData user1 = new UserData("userOne", "pass111", "one@mail.com");
    final LoginRequest user1Login = new LoginRequest("userOne", "pass111");
    final LoginRequest wrongPasswordLogin = new LoginRequest("userOne", "wrongPass");
    final UserData testSameUsername = new UserData("userOne", "differentPass", "different@mail.com");
    final UserData user2 = new UserData("userTwo", "pass222", "two@mail.com");
    final LoginRequest user2Login = new LoginRequest("userTwo", "pass222");

    @Test
    void registerValid() throws ResponseException {
        // requires login to work
        service.clear();
        service.register(user1);
        AuthData authData = service.login(user1Login);
        assertSame(authData.username(), user1.username());
    }

    @Test
    void registerExisting() throws ResponseException {
        service.clear();
        service.register(user1);
        assertThrows(RegistrationException.class, () ->
                service.register(testSameUsername));
    }

    @Test
    void loginValid() throws ResponseException {
        service.clear();
        service.register(user1);
        AuthData authData = service.login(user1Login);
        assertSame(authData.username(), user1.username());
        assertNotNull(authData.authToken());
    }

    @Test
    void loginWrongPassword() throws ResponseException {
        service.clear();
        service.register(user1);
        assertThrows(UnauthorizedException.class, () ->
                service.login(wrongPasswordLogin));
    }

    @Test
    void loginWrongUsername() throws ResponseException {
        service.clear();
        service.register(user1);
        assertThrows(UnauthorizedException.class, () ->
                service.login(user2Login));
    }
}
