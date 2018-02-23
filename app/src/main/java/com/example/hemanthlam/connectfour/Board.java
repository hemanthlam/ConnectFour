package com.example.hemanthlam.connectfour;

/**
 * Created by Jacob Crisan on 1/22/18.
 * File: Board.java
 * Purpose: to define an object class to represent the connect four game board.
 */

public class Board {
    // A multidimensional array to hold the connect four grid. It is a multi-dimensional array of booleans
    // (true indicates disc in appropriate part of grid, with false indicating otherwise)
    private int boardBounds[][];

    // The number of rows and columns in the grid
    private int height;
    private int width;

    // Constructors
    public Board() {
        boardBounds = null;
        height = 0;
        width = 0;
    }
    public Board(String size) {

        // Allocate disc array (boolean indicates if disc square is active or inactive)
        if (size.equalsIgnoreCase("7x6")) {
            this.boardBounds = new int[7][6];
            this.height = 6;
            this.width = 7;
        } else if (size.equalsIgnoreCase("8x7")) {
            this.boardBounds = new int[8][7];
            this.width = 8;
            this.height = 7;
        } else if (size.equalsIgnoreCase("10x8")) {
            this.boardBounds = new int[10][8];
            this.height = 8;
            this.width = 10;
        }
        // If no valid size is given, we can't allocate anything
        // It might be worth trying to throw an exception in the future, though I don't see this case happening very often
        else {
            this.boardBounds = null;
            this.height = 0;
            this.width = 0;
            return;
        }

        // Initialize disc array. Thankfully we don't need to resuse code!
        this.clearBoard();
    }

    // ClearBoard
    // INPUT: none
    // OUTPUT: none
    // Purpose: to clear the internal grid of data so we can start again
    public void clearBoard() {
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                this.boardBounds[i][j] = 0;
            }
        }
    }

    // findPosition
    // INPUT: col (int)
    // OUTPUT: int
    // Purpose: gets the index of the next availalble row in a specified column.
    //          When a disc is placed in that row, it no longer becomes available
    public int findPosition(int col, int player) {
        // Checking if given column is valid (if the index of the column exists in the array)
        // I am going to start an arbitrary error int return system, where -2 indicates invalid column input and -1 indicates that there are no available rows in the column
        if (col < 0 || col > (this.width-1))
            return -2;

        for (int i = height-1; i >= 0; --i) {
            if (this.boardBounds[col][i] == 0) {
                // Not sure what to do with the disc block when we discover its open, but this is a start (we can figure out what to do later)
                boardBounds[col][i] = player;
                return i;
            }
        }

        // If nothing was found (which is the case if we get here), return -1 to indicated no available rows were found
        return -1;
    }


    //checks for all the discs and compares to find if 4 are connected in a row
    public int[][] checkHorizontal(int player){
        int maxCol = this.width,
                maxRow = this.height,
                count =0;

        int[][] connectedFour = new int[4][2];
        for (int row = maxRow-1; row >= 0; row--) {
            count = 0;
            for(int col=0;col<maxCol;col++) {
                if (boardBounds[col][row] == player) {
                    connectedFour[count][0] = col;
                    connectedFour[count][1] = row;
                    count++;

                } else {
                    count = 0;
                    connectedFour = new int[4][2];
                }

                if (count >= 4)
                    return connectedFour;
            }
        }
        return null;
    }

    ////checks for all the discs and compares to find if 4 are connected in a column
    public int[][] checkVertical(int player){
        int maxCol = this.width,
                maxRow = this.height,
                count =0;

        int[][] connectedFour = new int[4][2];
        for (int col = 0; col<maxCol; col++) {
            for (int row = 0; row < maxRow; row++) {

                if (boardBounds[col][row] == player) {
                    connectedFour[count][0] = col;
                    connectedFour[count][1] = row;
                    count++;

                } else {
                    count = 0;
                    connectedFour = new int[4][2];
                }

                if (count >= 4)
                    return connectedFour;
            }
        }
        return null;
    }

    // check Diagonal Connected Four
    public int[][] checkDiagonal(int player){
        int[][] connectedFour=null;
        connectedFour = checkLeftTopToRightBottom(player);
        if(connectedFour!=null)
            return connectedFour;

        connectedFour = checkLeftBottomToRightTop(player);
        if(connectedFour!=null)
            return connectedFour;

        return connectedFour;
    }

    //Check all possibilities of connected four from left top to right bottom i.e 0,0 to maxRow,maxCol
    public int[][] checkLeftTopToRightBottom(int player){
        int maxCol = this.width,
                maxRow = this.height,
                count =0;

        int[][] connectedFour = new int[4][2];

        // Checking for connected four in left up center diagonals right diagonals
        for( int rowStart = 0; rowStart < maxRow; rowStart++){
            count = 0;
            for( int row = rowStart, col = 0; row < maxRow && col < maxCol; row++, col++ ){
                if(boardBounds[col][row] == player){
                    count++;
                    connectedFour[count-1][0] = col;
                    connectedFour[count-1][1] = row;
                    if(count >= 4) return connectedFour;
                }
                else {
                    count = 0;
                    connectedFour = new int[4][2];
                }
            }
        }

        // top-left to bottom-right - red diagonals
        // Checking for connected four in left up center diagonals left diagonals
        for( int colStart = 1; colStart < maxCol; colStart++){
            count = 0;
            int row, col;
            for( row = 0, col = colStart; row < maxRow && col < maxCol; row++, col++ ){
                if(boardBounds[col][row] == player){
                    count++;
                    connectedFour[count-1][0] = col;
                    connectedFour[count-1][1] = row;
                    if(count >= 4) return connectedFour;
                }
                else {
                    count = 0;
                    connectedFour = new int[4][2];
                }
            }
        }
        return null;
    }

    //Check all possibilities of connected four from left bottom to right top i.e maxRow,0 to 0,maxCol
    public int[][] checkLeftBottomToRightTop(int player){
        int maxCol = this.width,
                maxRow = this.height,
                count =0;

        int[][] connectedFour = new int[4][2];

        // Checking for connected four in left bottom center diagonals left diagonals
        for(int rowStart = maxRow-1; rowStart >=0; rowStart--){
            count = 0;
            int row, col;
            for( row = rowStart, col = 0; row >=0 && col < maxCol; row--, col++ ){
                if(boardBounds[col][row] == player){
                    count++;
                    connectedFour[count-1][0] = col;
                    connectedFour[count-1][1] = row;
                    if(count >= 4) return connectedFour;
                }
                else {
                    count = 0;
                    connectedFour = new int[4][2];
                }
            }
        }

        //checking for connected four in center diagonals' right diagonals
        for( int colStart = 0; colStart < maxCol; colStart++){
            count = 0;
            for(int row = maxRow-1, col = colStart; row >= 0 && col < maxCol; row--, col++ ){
                if(boardBounds[col][row] == player){
                    count++;
                    connectedFour[count-1][0] = col;
                    connectedFour[count-1][1] = row;
                    if(count >= 4) return connectedFour;
                }
                else {
                    count = 0;
                    connectedFour = new int[4][2];
                }
            }
        }
        return null;
    }

    //Checks to see if theres a winner for either player1 or player2
    //INPUT: The player with the current turn number (player1 == 1 and player2 == 2)
    //OUTPUT: The four (x,y) coordinates for the winning chips
    public int[][] findWinner(int player){
        int[][] connectedFour = null;
        connectedFour = checkHorizontal(player);
        if(connectedFour!=null)
            return connectedFour;

        connectedFour = checkVertical(player);
        if(connectedFour!=null)
            return connectedFour;

        connectedFour = checkDiagonal(player);
        if(connectedFour!=null)
            return connectedFour;

        return null;
    }

    //Checks to see if there's any room left on the board.
    //OUTPUT: If the board is full, return TRUE. If it's not, return FALSE.
    public boolean checkIfBoardFull(){
        for(int i=0;i<this.width;i++){
            if(boardBounds[i][0]==0){
                return false;
            }
        }
        return true;
    }


}

