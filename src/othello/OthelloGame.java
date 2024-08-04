package othello;
import javax.swing.SwingUtilities;

public class OthelloGame {
    public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> {
        GameUI.setupGUI();
       });
    }
}