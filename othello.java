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
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class othello extends JPanel {
    private static int size = 8;
    private static int icon_length = 70;
    private static final Color backgroundColor = Color.BLACK;     
    private static final Color cellColor = Color.GREEN.darker();    //Board Cell Baclground Color
    private static JLabel[][] labelGrid = new JLabel[size][size];   //Represent Cells with 8 x 8 Jlabels
    private static Icon blankIcon;
    private static Icon blackIcon;
    private static Icon whiteIcon;
    static JLabel Status = new JLabel("Status: Black begins");
    static JLabel blackCount = new JLabel("Black: 2");
    static JLabel whiteCount = new JLabel("White: 2");
    static JLabel totalCount = new JLabel("Total Disks: 4");
    static JLabel winner = new JLabel("Winner: ");
    static JLabel skip = new JLabel("Skip: false");
    static JButton resetButton = new JButton("Start Over");
    static JFrame gameOver = new JFrame("Result:");
    static JPanel result = new JPanel();

    //Gameplay variables
    private static String player = "black"; 
    private static int blackDiscs = 2;
    private static int whiteDiscs = 2;
    private static int totalDiscs = 4;
    private static boolean skipTurn = false;
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
                labelGrid[j][i] = label;
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

    public static void placeDisc(String color, int x, int y) {
        
        if (color == "black") {
            blackDiscs++;
            Status.setText("Status: White's turn.");
            blackCount.setText("Black: " + blackDiscs);
            System.out.printf("Jlabel[%d][%d] is " + player + "%n", x, y);
            labelGrid[x][y].setIcon(blackIcon);
            boardMatrix[x][y] = 1;
        }
        else {
            whiteDiscs++;
            Status.setText("Status: Black's turn");
            whiteCount.setText("White: " + whiteDiscs);
            System.out.printf("Jlabel[%d][%d] is " + player + "%n", x, y);
            labelGrid[x][y].setIcon(whiteIcon);
            labelGrid[x][y].setBackground(Color.BLUE);
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
                    if (label == labelGrid[j][i]) {
                        x = j;
                        y = i;
                        break;
                    }
                }
                if (x >= 0) {
                    break;
                }
            }

            System.out.println("Cell [" + x + "][" + y + "] Was clicked");
            if (x >= 0) {
                // Check if cell is empty or not
                Icon icon = label.getIcon();
          
                if (player == "black" && icon == blankIcon && !isGameOver() && GameLogic.isValid(1, x, y, boardMatrix)) {
                    othello.placeDisc(player, x, y);
                    othello.flipDiscs(x, y, 1);
                    resetCellsHighlighted();
                    
                    player = "white";
                    int[] aimove = GameLogic.miniMax(boardMatrix, 2, true);
                    System.out.println("MinMax Moves: " + aimove[1] + aimove[2] + "for value of " + aimove[0]);

                    placeDisc("white", aimove[1], aimove[2]);
                    flipDiscs(aimove[1], aimove[2], 2);

                    if (highlightPossibleMoves("black"))
                      player = "black";
                    else if (highlightPossibleMoves("white")) {
                      System.out.println("White Moves again");
                      skipTurn = true;
                      skip.setText("Skip: true");
                    }
                    othello.printBoard();
                }

                else if (skipTurn) {
                    int[] aimove = GameLogic.miniMax(boardMatrix, 2, true);
                    System.out.println("MinMax Moves: " + aimove[1] + aimove[2] + "for value of " + aimove[0]);
                    resetCellsHighlighted();

                    placeDisc("white", aimove[1], aimove[2]);
                    flipDiscs(aimove[1], aimove[2], 2);
                    highlightPossibleMoves("black");
                    printBoard();

                    if (highlightPossibleMoves("black"))
                       player = "black";
                }

                if (isGameOver()) {
                    Status.setText("Game Over");
                    if (blackDiscs > whiteDiscs)
                         winner.setText("You won.");
                    else
                         winner.setText("Computer Won.");
                         gameOver.setVisible(true);
                }

                blackCount.setText("Black: " + blackDiscs);
                whiteCount.setText("White: " + whiteDiscs);
	        totalCount.setText("Total Disks: " + (whiteDiscs + blackDiscs));
            }
        }
    }

    static othello mainPanel = new othello();

    private static void printBoard() {
        blackDiscs = 0;
        whiteDiscs = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(" " + boardMatrix[j][i]);
                if (boardMatrix[j][i] == 1){
                   blackDiscs++;
		   totalDiscs++;
                }
                else if (boardMatrix[j][i] == 2){
                   whiteDiscs++; 
		   totalDiscs++;
                }
            }
            System.out.println();
        }
    }

    private static void printBoardState(int[][] boardState) { //General version of printboard for debugging
      System.out.print("---------------------\n");
      for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
              System.out.print(" " + boardState[j][i]);
          }
          System.out.println();
      }
      System.out.println("---------------------\n");
    }

    private static void printValidMoves(char[][] boardState) { //General version of printboard for debugging
      System.out.print("///////////////////////\n");
      for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
              if (boardState[j][i] == 't')
                System.out.print(" " + boardState[j][i]);
              else
                System.out.print(" 0");

          }
          System.out.println();
      }
      System.out.println("/////////////////\n");
    }

   public static boolean highlightPossibleMoves(String player) {  //added boolean to see if player has a valid move
        boolean hasMove = false;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (GameLogic.isValid(getPlayerID(player), j, i,boardMatrix)) { 
                    labelGrid[j][i].setBackground(Color.GREEN);
                    hasMove = true;
                }
            }
        }
        return hasMove;
    }

    public static int getPlayerID(String player) {
        if (player == "black")
            return 1;

        return 2;
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
        whiteDiscs = 2;
        blackDiscs = 2;
        skipTurn = false;
        player = "black";
	totalCount.setText("Total Disks: " + (blackDiscs + whiteDiscs));

        //Initialize the board with 4 discs
        boardMatrix[3][3] = 1;
        boardMatrix[3][4] = 2;
        boardMatrix[4][4] = 1;
        boardMatrix[4][3] = 2;
       
        labelGrid[3][3].setIcon(blackIcon);;
        labelGrid[3][4].setIcon(whiteIcon);
        labelGrid[4][4].setIcon(blackIcon);
        labelGrid[4][3].setIcon(whiteIcon);

        // Highlight possible moves
        mainPanel.labelGrid[5][3].setBackground(Color.GREEN);
        mainPanel.labelGrid[4][2].setBackground(Color.GREEN);
        mainPanel.labelGrid[2][4].setBackground(Color.GREEN);
        mainPanel.labelGrid[3][5].setBackground(Color.GREEN);

        blackCount.setText("Black: " + blackDiscs);
        whiteCount.setText("White: " + whiteDiscs);
	totalCount.setText("Total Disks: " + (whiteDiscs + blackDiscs));
        Status.setText("Status: Black begins");
    }

    private static void resetCellsHighlighted() {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                mainPanel.labelGrid[j][i].setBackground(Color.GREEN.darker());
            }
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
        
        Stats.add(resetButton);
        Stats.addSeparator();
        Stats.add(Status);
        Stats.addSeparator();
        Stats.add(blackCount);
        Stats.addSeparator();
        Stats.add(whiteCount);
        Stats.addSeparator();
	Stats.add(totalCount);
        Stats.addSeparator();
        Stats.add(skip);
        Stats.setPreferredSize(new Dimension(100, 30));
        mainFrame.add(Stats, BorderLayout.PAGE_START);

	//Display Game Result
        result.add(winner, BorderLayout.CENTER);
        gameOver.getContentPane().add(result);
        gameOver.pack();
        gameOver.setVisible(false);
        gameOver.setSize(250, 100);
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
