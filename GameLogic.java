/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.ArrayList;

public class GameLogic {
    
     public static ArrayList<Integer> validMoves(int color, int[][] boardMatrix){ //returns list of valid moves as int array (I don't know a good way to make number pairs in Java yet)
        ArrayList<Integer> validMoveList = new ArrayList<>();
        for(int ypos = 0;ypos < 8;ypos++){
            for(int xpos = 0;xpos < 8;xpos++){
                if(isValid(color,xpos,ypos,boardMatrix)){ 
                    validMoveList.add(xpos);
                    validMoveList.add(ypos);
                }
            }
        }
            
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
                        if(boardMatrix[ix][iy] == 0)
                          break;
                        ix+=xpos;
                        iy+=ypos;
                    }
                }
                
            }
        return false;
    }
    
     public static ArrayList<Integer> validMoveDir(int color,int x, int y, int[][]boardMatrix){ //Return list of numpad directions in the direction of pieces to flip
        ArrayList<Integer> dirList = new ArrayList<>();                                        //1-bottom left, 2-bottom, 3-bottom-right etc 4-middle left etc.
        int keypad = 0;                                                                   //returns empty list if move is not valid
        int oppColor = 1;
        if(color == 1)
            oppColor = 2;
        
        
        for(int ypos = 1; ypos >= -1; ypos--)  {   //checks squares in direction left to right top to bottom                               
            for(int xpos = -1; xpos <= 1; xpos++) {
                keypad++;
                System.out.println("checking square[" + (x + xpos) + "][" + (y + ypos) + "]");
                if(xpos == 0 && ypos == 0) 
                    continue;            
                if(x + xpos < 0 || x + xpos > 7 || y + ypos < 0 || y + ypos > 7) //if adjacent square out of bounds then skip
                    continue;
                
                if(boardMatrix[x+xpos][y+ypos] == oppColor){
                    int ix = x+xpos;
                    int iy = y+ypos;
                    while(!(ix + xpos < 0 || ix + xpos > 7 || iy + ypos < 0 || iy + ypos > 7)){
                        System.out.println("|| checking square[" + ix + "][" + iy + "]");
                        if(boardMatrix[ix][iy] == color){  
                            dirList.add(keypad);
                            break;
                        }
                        ix+=xpos;
                        iy+=ypos;
                    }
                }
                
            }
        }
        return dirList; 
    }
      
         
}


