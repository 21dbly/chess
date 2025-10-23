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

    @Test
    void authorizeValid() throws ResponseException {
        service.clear();
        AuthData authData = service.register(user1);
        String username = service.authorize(authData.authToken());
        assertSame(username, authData.username());
    }

    @Test
    void authorizeMultiple() throws ResponseException {
        service.clear();
        AuthData authData1 = service.register(user1);
        AuthData authData2 = service.register(user2);
        String username1 = service.authorize(authData1.authToken());
        String username2 = service.authorize(authData2.authToken());
        assertSame(username1, authData1.username());
        assertSame(username2, authData2.username());
    }

    @Test
    void authorizeInvalid() throws ResponseException {
        service.clear();
        service.register(user1);
        assertThrows(UnauthorizedException.class, () ->
                service.authorize("123456789"));
    }

    @Test
    void logoutValid() throws ResponseException {
        service.clear();
        AuthData authData = service.register(user1);
        service.logout(authData.authToken());
        assertThrows(UnauthorizedException.class, () ->
                service.authorize(authData.authToken()));
    }

    @Test
    void logoutInvalid() throws ResponseException {
        service.clear();
        AuthData authData = service.register(user1);
        assertThrows(UnauthorizedException.class, () ->
                service.logout("123456789"));
    }
}
