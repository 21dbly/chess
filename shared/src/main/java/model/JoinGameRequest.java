package model;

public record JoinGameRequest(String playerColor, int gameID) {
    public boolean isValid() {
        if (playerColor == null) {
            return false;
        }
        return playerColor.equals("WHITE") || playerColor.equals("BLACK");
    }
}
