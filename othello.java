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
	static JLabel totalCount = new JLabel(" Total Disks: 0 ");
    static JButton resetButton = new JButton("Start Over");

    //Gameplay variables
    private static String player = "black"; 
    private static int blackDiscs = 2;
    private static int whiteDiscs = 2;
    private static int[][] boardMatrix = new int[8][8]; // 0 = blank, 1 = black, 2 = white
    private static int[][] affectedDiscs = new int[size][size];

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

    public static void placeDisc(String color,int x,int y) {
        
        if (color == "black") {
            labelGrid[x][y].setIcon(blackIcon);
            othello.boardMatrix[x][y] = 1;
            
        }
        else {
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
          
                if (player == "black" && icon == blankIcon && blackDiscs != 0 && GameLogic.isValid(1,x,y, boardMatrix)) {
                    othello.flipDiscs(x, y, 1);
                    label.setIcon(blackIcon);
                    blackDiscs ++;
                    Status.setText("Status: White's turn.");
                    blackCount.setText("Black: " + blackDiscs);
                    System.out.printf("Jlabel[%d][%d] is " + player + "%n",x,y);
                    othello.boardMatrix[x][y] = 1;
                    player = "white";
                    othello.printBoard();
                    resetCellsHighlighted();
                    highlightPossibleMoves("white");
                    if(isGameOver()){
                      System.out.println("Game is over");
                    }
                }
                else if (player == "white" && icon == blankIcon && whiteDiscs != 0 && GameLogic.isValid(2,x,y, boardMatrix)) {
                    othello.flipDiscs(x, y, 2);
                    label.setIcon(whiteIcon);
                    whiteDiscs ++;
                    Status.setText("Status: Black's turn");
                    whiteCount.setText("White: " + whiteDiscs);
                    System.out.printf("Jlabel[%d][%d] is " + player + "%n",x,y);
                    othello.boardMatrix[x][y] = 2;
                    player = "black";  
                    othello.printBoard();
                    resetCellsHighlighted();
                    highlightPossibleMoves("black");
                    if(isGameOver()){
                      System.out.println("Game is over");
                    }
                } else {
                  System.out.println("Move is not valid"); //GUI display later maybe
                }

            }
        }
    }

    static othello mainPanel = new othello();

    private static void printBoard() {
      
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(" " + mainPanel.boardMatrix[j][i]);
            }
            System.out.println();
        }
    }

    public static boolean isValidMove(String player, int x, int y) {
    if (boardMatrix[y][x] != 0) return false; // cell is not empty

    int opponent = player.equals("black") ? 2 : 1;

    int[][] directions = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},{0, 1},
        {1, -1}, {1, 0}, {1, 1}
    };

    for (int[] dir : directions) {
        int dx = dir[0], dy = dir[1];
        int curX = x + dx, curY = y + dy;

        boolean hasOpponent = false;
        while (curX >= 0 && curX < size && curY >= 0 && curY < size) {
            if (boardMatrix[curY][curX] == opponent) {
                hasOpponent = true;
            } else if (boardMatrix[curY][curX] == 0 || !hasOpponent) {
                break;
            } else if (boardMatrix[curY][curX] == 3 - opponent) {
                return true;
            }

            curX += dx;
            curY += dy;
            }
	 }

      return false;
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
        whiteDiscs = 2;
        blackDiscs = 2;
        player = "black";
		totalCount.setText("Total Disks: " + (blackDiscs + whiteDiscs));

        //Initialize the board with 4 discs
        mainPanel.placeDisc("black", 3,3);
        mainPanel.placeDisc("white", 3,4);
        mainPanel.placeDisc("black", 4,4);
        mainPanel.placeDisc("white", 4,3);

        // Highlight possible moves
        mainPanel.labelGrid[5][3].setBackground(Color.GREEN);
        mainPanel.labelGrid[4][2].setBackground(Color.GREEN);
        mainPanel.labelGrid[2][4].setBackground(Color.GREEN);
        mainPanel.labelGrid[3][5].setBackground(Color.GREEN);

        blackCount.setText("Black: " + blackDiscs);
        whiteCount.setText("White: " + whiteDiscs);
		totalCount.setText("   " +"Total Disks: " + (blackDiscs + whiteDiscs));
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
		Stats.add(totalCount);
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
