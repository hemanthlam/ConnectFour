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
    public int findPosition(int col) {
        // Checking if given column is valid (if the index of the column exists in the array)
        // I am going to start an arbitrary error int return system, where -2 indicates invalid column input and -1 indicates that there are no available rows in the column
        if (col < 0 || col > (this.width-1))
            return -2;

        for (int i = 0; i < this.height; ++i) {
            if (this.boardBounds[col][i] == 0) {
                // Not sure what to do with the disc block when we discover its open, but this is a start (we can figure out what to do later)
                boardBounds[col][i] = 1;
                return i;
            }
        }

        // If nothing was found (which is the case if we get here), return -1 to indicated no available rows were found
        return -1;
    }
}

