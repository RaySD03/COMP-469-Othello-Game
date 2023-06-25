import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class othello extends JPanel {
    private static int size = 8;
    private static int icon_length = 65;
    private static final Color backgroundColor = Color.BLACK;     
    private static final Color cellColor = Color.GREEN.darker();    //Board Cell Baclground Color
    private static JLabel[][] labelGrid = new JLabel[size][size];   //Represent Cells with 8 x 8 Jlabels
    private static Icon blankIcon;
    private static Icon blackIcon;
    private static Icon whiteIcon;
    static JLabel Status = new JLabel("Status: Black begins");
    static JLabel blackCount = new JLabel(" Black: 0 ");
    static JLabel whiteCount = new JLabel(" White: 0 ");
    static JButton resetButton = new JButton("Start Over");

    //Gameplay variables
    private static String activePlayer = "black"; 
    private static int blackDiscs = 0;
    private static int whiteDiscs = 0;
    private static int[][] boardMatrix = new int[8][8]; // 0 = blank, 1 = black, 2 = white

    public othello() {
        blankIcon = createIcon(new Color(0,0,0,0));
        blackIcon = createIcon(Color.BLACK);
        whiteIcon = createIcon(Color.WHITE);

        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setLayout(new GridLayout(size, size, 1, 1));

        MyMouse myMouse = new MyMouse();

        // Clickable JLables 
        for (int i = 0; i < labelGrid.length; i++) {
            for (int j = 0; j <labelGrid[i].length; j++) {
                JLabel label = new JLabel(blankIcon);
                label.setOpaque(true);
                label.setBackground(cellColor);
                label.addMouseListener(myMouse);
                labelGrid[i][j] = label;
                add(label);
            }
        }
    }

    private Icon createIcon(Color color) {
        BufferedImage img = new BufferedImage(icon_length, icon_length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        int gap = 4;
        int width = icon_length - 2 * gap;
        int height = width;
        g.fillOval(gap, gap, width, height); 
        g.dispose();
        return new ImageIcon(img);
    }

    private static void flipDiscs(int x, int y, int discColor) {
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

            while (curX >= 0 && curX < size && curY >= 0 && curY < size && boardMatrix[curX][curY] == 3 - discColor) {
                curX += dx;
                curY += dy;
            }

            if (curX >= 0 && curX < size && curY >= 0 && curY < size && boardMatrix[curX][curY] == discColor) {
                int flipX = x + dx;
                int flipY = y + dy;

                while (flipX != curX || flipY != curY) {
                    System.out.println("Flipping [" + flipX + "][" + flipY + "]");
                    boardMatrix[flipX][flipY] = discColor;
                    labelGrid[flipX][flipY].setIcon(discColor == 1 ? blackIcon : whiteIcon);
                    flipX += dx;
                    flipY += dy;
                    
                }
            }
        }
    }

    public static void placeDisc(String color,int x, int y) {
        
        if (color == "black") {
            
            blackDiscs++;
            Status.setText("Status: White's turn.");
            blackCount.setText("Black: " + blackDiscs);
            System.out.printf("Jlabel[%d][%d] is " + activePlayer + "%n",x,y);
            labelGrid[x][y].setIcon(blackIcon);
            boardMatrix[x][y] = 1;
   
        }
        else {
            whiteDiscs++;
            Status.setText("Status: Black's turn");
            whiteCount.setText("White: " + whiteDiscs);
            System.out.printf("Jlabel[%d][%d] is " + activePlayer + "%n",x,y);
            activePlayer = "black";  
            labelGrid[x][y].setIcon(whiteIcon);
            othello.boardMatrix[x][y] = 2;
            
        }
    }

   private class MyMouse extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
           
            JLabel label = (JLabel) e.getSource();    

            // Detect Which Cell Was Clicked
            int x = -1;
            int y = -1;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (label == labelGrid[i][j]) {
                        x = i;
                        y = j;
                        break;
                    }
                }
                if (x >= 0) {
                    break;
                }
            }
            
            if (x >= 0) {
                // Check if cell is empty or not
                Icon icon = label.getIcon();
          
                if (activePlayer == "black" && icon == blankIcon && !isGameOver() && logic.isValid(1, x, y, boardMatrix)) {

                    othello.placeDisc(activePlayer, x, y);
                    othello.flipDiscs(x, y, 1);
                    resetCellsHighlighted();
                   
                    activePlayer = "white";
                    miniMax(boardMatrix, 3, true);
                    placeDisc("white", bestX, bestY);
                    flipDiscs(bestX, bestY, 2);
                    highlightPossibleMoves("black");
                     othello.printBoard();
                    //bestX = 0;
                    //bestY = 0;
                }
            }
        }
    }

    // Class Instances
    static othello mainPanel = new othello();
    static GameLogic logic = new GameLogic();

    // Prints the current state of the Game board
    private static void printBoard() {
        
        blackDiscs = 0;
        whiteDiscs = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(" " + mainPanel.boardMatrix[i][j]);
                if(boardMatrix[j][i] == 1){
                  blackDiscs+=1;
				  blackCount.setText("black: " + blackDiscs);
                }
                else if(boardMatrix[j][i] == 2){
                  whiteDiscs++;  
                  whiteCount.setText("white: " + whiteDiscs);
				  
                }
            }
            System.out.println();
        }
    }

    public static int getPlayerID(String player) {
        if (player == "black")
            return 1;

        return 2;
    }
 
    public static void highlightPossibleMoves(String player) {

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(logic.isValid(getPlayerID(player),i,j,boardMatrix) && boardMatrix[i][j] == 0) {
                    labelGrid[i][j].setBackground(Color.GREEN);
                }
            }
        }
    }

    public static Boolean isGameOver() {
        if (whiteDiscs + blackDiscs == size * size) { // Check if all discs are placed
            return true;
        }

        else {
            return false;
        }
    }

    private static void setupGame() {
        resetCellsHighlighted(); 

        //Initialize the boardMatrix with 0s
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardMatrix[j][i] = 0;
                labelGrid[j][i].setIcon(blankIcon);        
            }
        }
        whiteDiscs = 0;
        blackDiscs = 0;
        activePlayer = "black";

        //Initialize the board with 4 discs
        mainPanel.boardMatrix[3][3] = 1;
        mainPanel.boardMatrix[3][4] = 2;
        mainPanel.boardMatrix[4][4] = 1;
        mainPanel.boardMatrix[4][3] = 2;

        mainPanel.labelGrid[3][3].setIcon(blackIcon);
        mainPanel.labelGrid[3][4].setIcon(whiteIcon);
        mainPanel.labelGrid[4][4].setIcon(blackIcon);
        mainPanel.labelGrid[4][3].setIcon(whiteIcon);

        // Highlight possible moves
        mainPanel.labelGrid[5][3].setBackground(Color.GREEN);
        mainPanel.labelGrid[4][2].setBackground(Color.GREEN);
        mainPanel.labelGrid[2][4].setBackground(Color.GREEN);
        mainPanel.labelGrid[3][5].setBackground(Color.GREEN);

        blackCount.setText("Black: " + blackDiscs);
        whiteCount.setText("White: " + whiteDiscs);
        Status.setText("Status: Black begins");
    }

    public static int[][] copyBoard(int[][] gameBoard) {
        int[][] resultBoard = new int[8][8];
      
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                resultBoard[i][j] = gameBoard[i][j];
            }
        }

        return resultBoard;
    }

    private static int[][] flipCloneBoardDiscs(int x, int y, int discColor, int[][] Board) {
       int[][] resultBoard = new int [size][size];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                resultBoard[i][j] = Board[i][j];
            }
        }
       
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

            while (curX >= 0 && curX < size && curY >= 0 && curY < size && resultBoard[curX][curY] == 3 - discColor) {
                curX += dx;
                curY += dy;
            }

            if (curX >= 0 && curX < size && curY >= 0 && curY < size && resultBoard[curX][curY] == discColor) {
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

    public static int[][] processNewBoard(int[][] Board, int x, int y, int PlayerID) {

        Board[x][y] = PlayerID;
        Board = flipCloneBoardDiscs(x, y, PlayerID, Board);

        return Board;
    }

    public static int evaluate(int[][] Board) {
        int black = 0;
        int white = 0;

         for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {   
                if (Board[j][i] == 1) {
                    black++;
                }
                else if (Board[j][i] == 2) {
                    white++;
                }
            }
        }

        int cornerWeight = 10;

        if (Board[0][0] == 1) {
            black += cornerWeight;
        }

        if (Board[0][7] == 1) {
            black += cornerWeight;
        }

        if (Board[7][0] == 1) {
            black += cornerWeight;
        }

        if (Board[7][7] == 1) {
            black += cornerWeight;
        }

        if (Board[0][0] == 2) {
            white += cornerWeight;
        }

        if (Board[0][7] == 2) {
            white += cornerWeight;
        }

        if (Board[7][0] == 2) {
            white += cornerWeight;
        }

        if (Board[7][7] == 2) {
            black += cornerWeight;
        }
        

        return white - black;
    }

    private static void resetCellsHighlighted() {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                labelGrid[j][i].setBackground(Color.GREEN.darker());
            }
        }
    }

    public static int bestX = 0;
    public static int bestY = 0;

    public static int miniMax(int[][] game, int depth, Boolean maximizing) {
   

        if (depth == 0) {
            return maximizing ? evaluate(game) : -evaluate(game);
        }

        if (maximizing) {
            int bestValue = Integer.MIN_VALUE;
            char[][] validList = logic.validMoves(2, game); // generate all possible moves for player
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    
                  if (validList[j][i] == 't' && game[j][i] == 0) {
                     int[][] newStateBoard = copyBoard(game);
                     if (logic.isValid(2, j, i, newStateBoard)) {
                         processNewBoard(newStateBoard, j, i, 2);
                     }
                     
                     int value = miniMax(newStateBoard, depth - 1, maximizing);

                     if (value > bestValue) {
                        bestValue = value;
                        bestX = i;
                        bestY = j;
                        //System.out.printf("Best Coordinate %d %d", i, j);
                     }
                  }
                }
            }
           
            return bestValue;
        }

        else {
            int bestValue = Integer.MAX_VALUE;
            char[][] validList = logic.validMoves(2, game); // generate all possible moves for player
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    
                  if (validList[j][i] == 't'  && game[j][i] == 0) {
                     int[][] newStateBoard = copyBoard(game);
                     if (logic.isValid(2, j, i, newStateBoard)) {
                         processNewBoard(newStateBoard, j, i, 2);
                     }

                     int value = miniMax(newStateBoard, depth - 1, !maximizing);

                     if (value < bestValue) {
                        bestValue = value;
                        bestX = i;
                        bestY = j;
                        //System.out.printf("Best Coordinate %d %d", i, j);
                     }
                  }
                }
            }
            return bestValue;
        }
    }

    private static void setupGUI() {
        resetCellsHighlighted();
        setupGame();

        //Create the game window
        JFrame mainFrame = new JFrame("Othello");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);

        //Add Status Bar
        JToolBar Stats = new JToolBar();
        Stats.setFloatable(false);
        Stats.setPreferredSize(new Dimension(100, 35));
        
        Stats.add(resetButton);
        Stats.addSeparator();
        Stats.add(Status);
        Stats.addSeparator();
        Stats.add(blackCount);
        Stats.addSeparator();
        Stats.add(whiteCount);
        mainFrame.add(Stats, BorderLayout.PAGE_START);

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> setupGUI());  
        
        //MouseEvent for reseting game
        resetButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("Restart Game.");
                setupGame();

            }
        });
    }
}