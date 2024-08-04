package othello;
import javax.swing.*;

public class Board {
    private static final int SIZE = 8;
    private int[][] boardMatrix;
    private static JLabel[][] labelGrid = GameUI.getLabelGrid();

    public Board() {
        boardMatrix = new int[SIZE][SIZE];
        setupGame();
    }

    public int[][] getBoardMatrix() {
        return boardMatrix;
    }

    public void placeDisc(String color, int x, int y) {
        if (color.equals("black")) {
            boardMatrix[x][y] = 1;
            labelGrid[x][y].setIcon(GameUI.getBlackIcon());
            GameUI.getBlackPlayer().incrementDiscCount();
        } else {
            boardMatrix[x][y] = 2;
            labelGrid[x][y].setIcon(GameUI.getWhiteIcon());
            GameUI.getWhitePlayer().incrementDiscCount();
        }
        GameLogic.flipDiscs(x, y, color.equals("black") ? 1 : 2, boardMatrix);
        GameUI.updateDiscCounts();
    
        int nextPlayerColor = color.equals("black") ? 2 : 1;
        if (GameLogic.hasValidMoves(nextPlayerColor, boardMatrix)) {
            GameUI.highlightValidMoves(nextPlayerColor); // Highlight valid moves for the next player
            GameUI.setCurrentPlayer(nextPlayerColor == 1 ? GameUI.getBlackPlayer() : GameUI.getWhitePlayer());
        } else if (GameLogic.hasValidMoves(color.equals("black") ? 1 : 2, boardMatrix)) {
            GameUI.setStatus("No valid moves for " + (nextPlayerColor == 1 ? "White" : "Black") + ". " + (color.equals("black") ? "Black" : "White") + " continues.");
            GameUI.highlightValidMoves(color.equals("black") ? 1 : 2); // Highlight valid moves for the current player
        } else {
            GameLogic.determineWinner();
        }
        printBoardMatrix();
    }   
    
    public void setupGame() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                boardMatrix[i][j] = 0;
                labelGrid[i][j].setIcon(GameUI.getBlankIcon());
            }
        }
       // Place initial discs without incrementing the count (do not use placeDisc)
        boardMatrix[3][3] = 1;
        labelGrid[3][3].setIcon(GameUI.getBlackIcon());
        boardMatrix[3][4] = 2;
        labelGrid[3][4].setIcon(GameUI.getWhiteIcon());
        boardMatrix[4][4] = 1;
        labelGrid[4][4].setIcon(GameUI.getBlackIcon());
        boardMatrix[4][3] = 2;
        labelGrid[4][3].setIcon(GameUI.getWhiteIcon());

        // Set initial disc count
        GameUI.getBlackPlayer().setDiscCount(2);
        GameUI.getWhitePlayer().setDiscCount(2);
        GameUI.updateDiscCounts();
    }

    // Debugging purposes
    private void printBoardMatrix() {
        System.out.println("Current Board Matrix:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(boardMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}