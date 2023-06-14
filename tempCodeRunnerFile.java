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
        flipDiscs(x, y, color.equals("black") ? 1 : 2);
        resetCellsHighlighted();
    }