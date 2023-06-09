package Othello;
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
    private static int icon_length = 55;
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

    public static void placeDisc(String color,int x,int y) {
        
        if (color == "black") {
            labelGrid[y][x].setIcon(blackIcon);
            othello.boardMatrix[x][y] = 1;
            blackDiscs ++;
            Status.setText("Status: White's turn.");
            blackCount.setText("Black: " + blackDiscs);
            System.out.printf("Jlabel[%d][%d] is " + color + "%n",x,y);
            player = "white";
            othello.printBoard();
            resetCellsHighlighted();
        }
        else {
            labelGrid[y][x].setIcon(whiteIcon);
            othello.boardMatrix[x][y] = 2;
            whiteDiscs ++;
            highlightPossibleMoves("black");
            Status.setText("Status: Black's turn");
            whiteCount.setText("White: " + whiteDiscs);
            System.out.printf("Jlabel[%d][%d] is " + player + "%n",x,y);
            player = "black";  
            othello.printBoard();
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
                        x = j;
                        y = i;
                        break;
                    }
                }
                if (x >= 0) {
                    break;
                }
            }
            
            if (x >= 0) {
                // Check if cell is empty or not

                if (player == "black" && isValidMove("black",x,y) && !isGameOver()) {
                    //label.setIcon(blackIcon);
                    placeDisc("black", x, y);
                  
                }
                else if (player == "white" && isValidMove("white",x,y) && !isGameOver()) {
                    //label.setIcon(whiteIcon);

                    placeDisc("white", x, y);

                }
                else {
                    return;
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

    public static Boolean isValidMove(String player, int row, int column) {

        int opponent = 1;
        if (player == "black") {
            opponent = 2;
        }

        // If cell is empty
        if (boardMatrix[row][column] == 0) {
            if (row + 1 < 8 && column + 1 < 8 && boardMatrix[row + 1][column + 1] == opponent) {
                return true;
            }
            else if (row + 1 < 8  && boardMatrix[row + 1][column] == opponent) {
                return true;
            }
            else if (column + 1 < 8 && boardMatrix[row][column + 1] == opponent) {
                return true;
            }
            else if (column - 1 > -1 && boardMatrix[row][column - 1] == opponent) {
                return true;
            }
            else if (row - 1 > -1 && column - 1 > -1  && boardMatrix[row - 1][column - 1] == opponent) {
                return true;
            }
            else if (row - 1 > -1 && boardMatrix[row - 1][column] == opponent) {
                return true;
            }
            else if (row - 1 > -1 && column + 1 < 8  && boardMatrix[row - 1][column + 1] == opponent) {
                return true;
            }
            else if (row + 1 < 8 && column - 1 > -1 && boardMatrix[row + 1][column - 1] == opponent) {
                return true;
           }
        }
        return false;
    }

    public static void highlightPossibleMoves(String player) {

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(isValidMove(player,i,j)) {
                    labelGrid[j][i].setBackground(Color.GREEN);
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
        Status.setText("Status: Black begins");
    }

    private static void resetCellsHighlighted() {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                labelGrid[j][i].setBackground(Color.GREEN.darker());
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
