package othello;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class GameLogic {
    public static boolean isGameOver(int[][] boardMatrix) {
        // Check if there any valid moves remain for either player
        ArrayList<CoordPair> blackMoves = validMoves(1, boardMatrix);
        ArrayList<CoordPair> whiteMoves = validMoves(2, boardMatrix);
        return blackMoves.isEmpty() && whiteMoves.isEmpty();
    }

    public static void determineWinner() {
        int blackCount = GameUI.getBlackPlayer().getDiscCount();
        int whiteCount = GameUI.getWhitePlayer().getDiscCount();
        String message;
        if (blackCount > whiteCount) {
            message = "Game Over: Black wins!";
        } else if (whiteCount > blackCount) {
            message = "Game Over: White wins!";
        } else {
            message = "Game Over: It's a tie!";
        }
        GameUI.setStatus(message);
        JOptionPane.showMessageDialog(null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean hasValidMoves(int playerColor, int[][] boardMatrix) {
        ArrayList<CoordPair> validMoves = validMoves(playerColor, boardMatrix);
        return !validMoves.isEmpty();
    }

    public static ArrayList<CoordPair> validMoves(int color, int[][] board) {
        ArrayList<CoordPair> validList = new ArrayList<>();
        for (int ypos = 0; ypos < 8; ypos++) {
            for (int xpos = 0; xpos < 8; xpos++) {
                if (isValid(color, xpos, ypos, board)) {
                    validList.add(new CoordPair(xpos, ypos));
                }
            }
        }
        return validList;
    }

    public static boolean isValid(int color, int x, int y, int[][] boardMatrix) {
        int oppColor = (color == 1) ? 2 : 1;
        if (boardMatrix[x][y] != 0) return false;
        for (int ypos = -1; ypos <= 1; ypos++) {
            for (int xpos = -1; xpos <= 1; xpos++) {
                if (xpos == 0 && ypos == 0) continue;
                if (x + xpos < 0 || x + xpos > 7 || y + ypos < 0 || y + ypos > 7) continue;
                if (boardMatrix[x + xpos][y + ypos] == oppColor) {
                    int ix = x + xpos;
                    int iy = y + ypos;
                    while (!(ix + xpos < 0 || ix + xpos > 7 || iy + ypos < 0 || iy + ypos > 7)) {
                        if (boardMatrix[ix][iy] == color) return true;
                        if (boardMatrix[ix][iy] == 0) break;
                        ix += xpos;
                        iy += ypos;
                    }
                }
            }
        }
        return false;
    }

    public static void flipDiscs(int x, int y, int discColor, int[][] boardMatrix) {
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
        };
    
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int curX = x + dx;
            int curY = y + dy;
    
            while (curX >= 0 && curX < 8 && curY >= 0 && curY < 8 && boardMatrix[curX][curY] == 3 - discColor) {
                curX += dx;
                curY += dy;
            }
    
            if (curX >= 0 && curX < 8 && curY >= 0 && curY < 8 && boardMatrix[curX][curY] == discColor) {
                int flipX = x + dx;
                int flipY = y + dy;
    
                while (flipX != curX || flipY != curY) {
                    boardMatrix[flipX][flipY] = discColor;
                    GameUI.getLabelGrid()[flipX][flipY].setIcon(discColor == 1 ? GameUI.getBlackIcon() : GameUI.getWhiteIcon());
                    if (discColor == 1) {
                        GameUI.getBlackPlayer().incrementDiscCount();
                        GameUI.getWhitePlayer().decrementDiscCount();
                    } else {
                        GameUI.getWhitePlayer().incrementDiscCount();
                        GameUI.getBlackPlayer().decrementDiscCount();
                    }
                    flipX += dx;
                    flipY += dy;
                }
            }
        }
    }    

    public static int[][] copyBoard(int[][] gameBoard) {
        int[][] resultBoard = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(gameBoard[i], 0, resultBoard[i], 0, 8);
        }
        return resultBoard;
    }

    public static int[][] processNewBoard(int[][] board, int x, int y, int playerID) {
        board[x][y] = playerID;
        return flipCloneBoardDiscs(x, y, playerID, board);
    }

    private static int[][] flipCloneBoardDiscs(int x, int y, int discColor, int[][] board) {
        int[][] resultBoard = copyBoard(board);
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int curX = x + dx;
            int curY = y + dy;

            while (curX >= 0 && curX < 8 && curY >= 0 && curY < 8 && resultBoard[curX][curY] == 3 - discColor) {
                curX += dx;
                curY += dy;
            }

            if (curX >= 0 && curX < 8 && curY >= 0 && curY < 8 && resultBoard[curX][curY] == discColor) {
                int flipX = x + dx;
                int flipY = y + dy;

                while (flipX != curX || flipY != curY) {
                    resultBoard[flipX][flipY] = discColor;
                    flipX += dx;
                    flipY += dy;
                }
            }
        }

        return resultBoard;
    }

    public static int staticWeightHeuristic(int[][] board) {
        int black = 0;
        int white = 0;

        int[][] boardweight = {
            {4, -3, 2, 2, 2, 2, -3, 4},
            {-3, -4, -1, -1, -1, -1, -4, -3},
            {2, -1, 1, 0, 0, 1, -1, 2},
            {2, -1, 0, 1, 1, 0, -1, 2},
            {2, -1, 0, 1, 1, 0, -1, 2},
            {2, -1, 1, 0, 0, 1, -1, 2},
            {-3, -4, -1, -1, -1, -1, -4, -3},
            {4, -3, 2, 2, 2, 2, -3, 4}
        };

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[j][i] == 1) {
                    black += boardweight[j][i];
                } else if (board[j][i] == 2) {
                    white += boardweight[j][i];
                }
            }
        }
        return white - black;
    }

    public static int cornerWeightHeuristic(int[][] board) {
        int black = 0;
        int white = 0;
        int cornerWeight = 10;

        if (board[0][0] == 1) black += cornerWeight;
        if (board[0][7] == 1) black += cornerWeight;
        if (board[7][0] == 1) black += cornerWeight;
        if (board[7][7] == 1) black += cornerWeight;

        if (board[0][0] == 2) white += cornerWeight;
        if (board[0][7] == 2) white += cornerWeight;
        if (board[7][0] == 2) white += cornerWeight;
        if (board[7][7] == 2) white += cornerWeight;

        return white - black;
    }

    public static int evaluate(int[][] boardState, int heuristic) {
        if (heuristic == 1) return cornerWeightHeuristic(boardState);
        if (heuristic == 2) return staticWeightHeuristic(boardState);
        return 0;
    }

    public static int[] miniMax(int[][] game, int depth, boolean maximizing, int heuristic) {
        int[] result = new int[4];

        if (depth == 0) {
            result[0] = evaluate(game, heuristic);
            result[3] = 1;
            return result;
        }

        if (maximizing) {
            int bestValue = Integer.MIN_VALUE;
            ArrayList<CoordPair> validList = validMoves(2, game);
            for (CoordPair move : validList) {
                int[][] newStateBoard = processNewBoard(copyBoard(game), move.x, move.y, 2);
                int[] value = miniMax(newStateBoard, depth - 1, false, heuristic);
                result[3] += value[3];

                if (value[0] > bestValue) {
                    bestValue = value[0];
                    result[0] = value[0];
                    result[1] = move.x;
                    result[2] = move.y;
                }
            }
            return result;
        } else {
            int bestValue = Integer.MAX_VALUE;
            ArrayList<CoordPair> validList = validMoves(1, game);
            for (CoordPair move : validList) {
                int[][] newStateBoard = processNewBoard(copyBoard(game), move.x, move.y, 1);
                int[] value = miniMax(newStateBoard, depth - 1, true, heuristic);
                result[3] += value[3];

                if (value[0] < bestValue) {
                    bestValue = value[0];
                    result[0] = value[0];
                    result[1] = move.x;
                    result[2] = move.y;
                }
            }
            return result;
        }
    }

    public static int[] miniMaxAB(int[][] game, int depth, boolean maximizing, int alpha, int beta, int heuristic) {
        int[] result = new int[4];

        if (depth == 0) {
            result[0] = evaluate(game, heuristic);
            result[3] = 1;
            return result;
        }

        if (maximizing) {
            int bestValue = Integer.MIN_VALUE;
            ArrayList<CoordPair> validList = validMoves(2, game);
            for (CoordPair move : validList) {
                int[][] newStateBoard = processNewBoard(copyBoard(game), move.x, move.y, 2);
                int[] value = miniMaxAB(newStateBoard, depth - 1, false, alpha, beta, heuristic);
                result[3] += value[3];

                if (value[0] > bestValue) {
                    bestValue = value[0];
                    result[0] = value[0];
                    result[1] = move.x;
                    result[2] = move.y;
                }
                if (bestValue >= beta) {
                    result[0] = bestValue;
                    result[1] = move.x;
                    result[2] = move.y;
                    return result;
                }
                                alpha = Math.max(alpha, bestValue);
            }
            return result;
        } else {
            int bestValue = Integer.MAX_VALUE;
            ArrayList<CoordPair> validList = validMoves(1, game);
            for (CoordPair move : validList) {
                int[][] newStateBoard = processNewBoard(copyBoard(game), move.x, move.y, 1);
                int[] value = miniMaxAB(newStateBoard, depth - 1, true, alpha, beta, heuristic);
                result[3] += value[3];

                if (value[0] < bestValue) {
                    bestValue = value[0];
                    result[0] = value[0];
                    result[1] = move.x;
                    result[2] = move.y;
                }
                if (bestValue <= alpha) {
                    result[0] = bestValue;
                    result[1] = move.x;
                    result[2] = move.y;
                    return result;
                }
                beta = Math.min(beta, bestValue);
            }
            return result;
        }
    }
}