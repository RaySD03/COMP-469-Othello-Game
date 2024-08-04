package othello;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

public class GameUI extends JPanel {
    private static final int SIZE = 8;
    private static final int ICON_LENGTH = 68;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color CELL_COLOR = Color.GREEN.darker();
    private static JLabel[][] labelGrid = new JLabel[SIZE][SIZE];
    private static Icon blankIcon;
    private static Icon blackIcon;
    private static Icon whiteIcon;
    private static JLabel status = new JLabel("Status: Black begins");
    private static JLabel blackCount = new JLabel(" Black: 0 ");
    private static JLabel whiteCount = new JLabel(" White: 0 ");
    private static JLabel totalCount = new JLabel(" Total Disks: 0 ");
    private static JButton resetButton = new JButton("Start Over");
    private static Board board;
    private static boolean gridEnabled = true;
    private static Player blackPlayer = new Player("black");
    private static Player whitePlayer = new Player("white");
    private static Player currentPlayer;

    public GameUI() {
        blankIcon = createIcon(new Color(0, 0, 0, 0));
        blackIcon = createIcon(Color.BLACK);
        whiteIcon = createIcon(Color.WHITE);

        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setLayout(new GridLayout(SIZE, SIZE, 1, 1));

        MyMouse myMouse = new MyMouse();

        for (int i = 0; i < labelGrid.length; i++) {
            for (int j = 0; j < labelGrid[i].length; j++) {
                JLabel label = new JLabel(blankIcon);
                label.setOpaque(true);
                label.setBackground(CELL_COLOR);
                label.addMouseListener(myMouse);
                labelGrid[i][j] = label;
                add(label);
            }
        }

        board = new Board();
    }

    public static JLabel[][] getLabelGrid() {
        return labelGrid;
    }

    public static Icon getBlankIcon() {
        return blankIcon;
    }

    public static Icon getBlackIcon() {
        return blackIcon;
    }

    public static Icon getWhiteIcon() {
        return whiteIcon;
    }

    public static Player getBlackPlayer() {
        return blackPlayer;
    }
    
    public static Player getWhitePlayer() {
        return whitePlayer;
    }

    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    public static void setStatus(String message) {
        status.setText(message);
    }
    
    public static void updateDiscCounts() {
        blackCount.setText(" Black: " + blackPlayer.getDiscCount() + " ");
        whiteCount.setText(" White: " + whitePlayer.getDiscCount() + " ");
        totalCount.setText(" Total Disks: " + (blackPlayer.getDiscCount() + whitePlayer.getDiscCount()) + " ");
    }    

    private Icon createIcon(Color color) {
        BufferedImage img = new BufferedImage(ICON_LENGTH, ICON_LENGTH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        int gap = 4;
        int width = ICON_LENGTH - 2 * gap;
        int height = width;
        g.fillOval(gap, gap, width, height);
        g.dispose();
        return new ImageIcon(img);
    }

    private class MyMouse extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (!gridEnabled) {
                return; // Ignore clicks if the grid is disabled
            }
    
            JLabel label = (JLabel) e.getSource();
            int x = -1;
            int y = -1;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
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
    
            if (x >= 0) {
                Icon icon = label.getIcon();
                if (currentPlayer.getColor().equals("black") && icon == blankIcon && GameLogic.isValid(1, x, y, board.getBoardMatrix())) {
                    board.placeDisc("black", x, y);
                    label.setIcon(blackIcon);
                    status.setText("Status: White's turn");
                    currentPlayer = whitePlayer;
                    aiMove();
                } else if (currentPlayer.getColor().equals("white") && icon == blankIcon && GameLogic.isValid(2, x, y, board.getBoardMatrix())) {
                    board.placeDisc("white", x, y);
                    label.setIcon(whiteIcon);
                    status.setText("Status: Black's turn");
                    currentPlayer = blackPlayer;
                    aiMove();
                }
            }
        }
    }
 
    private void aiMove() {
        // Disable grid cells when opponent is placing a disc
        setGridEnabled(false);
    
        Timer timer = new Timer(1000, e -> {
            int[] move;
            if (currentPlayer.getColor().equals("black")) {
                move = GameLogic.miniMaxAB(board.getBoardMatrix(), 3, false, Integer.MIN_VALUE, Integer.MAX_VALUE, 2);
                board.placeDisc("black", move[1], move[2]);
                labelGrid[move[1]][move[2]].setIcon(blackIcon);
                status.setText("Status: White's turn");
                currentPlayer = whitePlayer;
            } else {
                move = GameLogic.miniMaxAB(board.getBoardMatrix(), 3, true, Integer.MIN_VALUE, Integer.MAX_VALUE, 2);
                board.placeDisc("white", move[1], move[2]);
                labelGrid[move[1]][move[2]].setIcon(whiteIcon);
                status.setText("Status: Black's turn");
                currentPlayer = blackPlayer;
            }
    
            // Re-enable grid cells
            setGridEnabled(true);
        });
        timer.setRepeats(false); // Ensure the timer only runs once
        timer.start();
    }
    
    private void setGridEnabled(boolean enabled) {
        gridEnabled = enabled;
    }    

    public static void highlightValidMoves(int playerColor) {
        // Clear previous highlights
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                labelGrid[i][j].setBackground(CELL_COLOR);
            }
        }

        // Get valid moves
        ArrayList<CoordPair> validMoves = GameLogic.validMoves(playerColor, board.getBoardMatrix());

        // Highlight the valid moves
        for (CoordPair move : validMoves) {
            labelGrid[move.x][move.y].setBackground(Color.GREEN); // Change cell color
        }
    }

    public static void setupGUI() {
        JFrame mainFrame = new JFrame("Othello");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(new GameUI());
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null); // Center the game window
        mainFrame.setVisible(true);

        JToolBar stats = new JToolBar();
        stats.setFloatable(false);
        stats.add(resetButton);
        stats.addSeparator();
        stats.add(status);
        stats.addSeparator();
        stats.add(blackCount);
        stats.addSeparator();
        stats.add(whiteCount);
        stats.addSeparator();
        stats.add(totalCount);
        mainFrame.add(stats, BorderLayout.PAGE_START);

        resetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                board.setupGame();
                //choosePlayerColor(); // Prompt the user to choose a color again
                GameUI.highlightValidMoves(currentPlayer.getColor().equals("black") ? 1 : 2); // Highlight valid moves for the starting player
            }
        });

        choosePlayerColor();
        GameUI.highlightValidMoves(currentPlayer.getColor().equals("black") ? 1 : 2); // Highlight valid moves for the starting player
    }

    private static void choosePlayerColor() {
        String[] options = {"Black", "White"};
        int choice = JOptionPane.showOptionDialog(null, "Choose your color:", "Player Color",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            currentPlayer = blackPlayer;
            status.setText("Status: Black begins");
        } else {
            currentPlayer = whitePlayer;
            status.setText("Status: White begins");
        }
    }
}