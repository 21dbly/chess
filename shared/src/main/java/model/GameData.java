package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public String getUsername(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return whiteUsername;
        } else {
            return blackUsername;
        }
    }
}
