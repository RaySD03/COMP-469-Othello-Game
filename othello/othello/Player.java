package othello;

public class Player {
    private String color;
    private int discCount;

    public Player(String color) {
        this.color = color;
        this.discCount = 2; // Initial disc count
    }

    public String getColor() {
        return color;
    }

    public int getDiscCount() {
        return discCount;
    }

    public void setDiscCount(int discCount) {
        this.discCount = discCount;
    }

    public void incrementDiscCount() {
        this.discCount++;
    }

    public void decrementDiscCount() {
        this.discCount--;
    }
}