/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Othello;
import java.util.ArrayList;

public class GameLogic {
    
    public static ArrayList<int[]> validMoves(String color, int[][] boardMatrix){
        ArrayList<int[]> validMoveList = new ArrayList<int[]>();
        
        return validMoveList;
    }
    
    public static boolean isValid(int color,int x, int y, int[][] boardMatrix){  //given color, position and board determine if move is valid
        int oppColor = 1;
        if(color == 1)
            oppColor = 2;
        
        for(int ypos = -1; ypos <= 1; ypos++)                                    
            for(int xpos = -1; xpos <= 1; xpos++) {
                //System.out.println("checking square[" + (x + xpos) + "][" + (y + ypos) + "]");
                if(xpos == 0 && ypos == 0) 
                    continue;            
                if(x + xpos < 0 || x + xpos > 7 || y + ypos < 0 || y + ypos > 7) //if adjacent square out of bounds then skip
                    continue;
                
                if(boardMatrix[x+xpos][y+ypos] == oppColor){ //If adjacent square is opposite color-
                    int ix = x+xpos;
                    int iy = y+ypos;
                    while(!(ix + xpos < 0 || ix + xpos > 7 || iy + ypos < 0 || iy + ypos > 7)){ //start search in direction for same color same color if same color
                        //System.out.println("|| checking square[" + ix + "][" + iy + "]");
                        if(boardMatrix[ix][iy] == color)  
                            return true;
                        ix+=xpos;
                        iy+=ypos;
                    }
                }
                
            }
        return false;
    }
         
}


