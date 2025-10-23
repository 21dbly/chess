package model;

public record UserData(String username, String password, String email) {
    public boolean isComplete() {
        return username != null &&
                !username.isEmpty() &&
                password != null &&
                !password.isEmpty() &&
                email != null &&
                !email.isEmpty();
    }
}
