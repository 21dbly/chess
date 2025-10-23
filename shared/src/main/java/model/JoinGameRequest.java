package model;

public record JoinGameRequest(String playerColor, int gameID) {
    public boolean isValid() {
        return playerColor.equals("WHITE") || playerColor.equals("BLACK");
    }
}
