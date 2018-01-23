package com.example.hemanthlam.connectfour;

/**
 * Created by Jacob Crisan on 1/22/18.
 * File: Board.java
 * Purpose: to define an object class to represent the connect four game board. This should be able to handle game board operations
 */

public class Board {
    // A multidimensional array to hold the connect four grid. It is a multi-dimensional array of booleans
    // (true indicates disc in appropriate part of grid, with false indicating otherwise)
    private boolean discSquareArr[][];

    // An array that holds the next available disc space in the grid.
    // Each index of the array represents a column of the grid, and the integer number at that position represents the index of the next available
    // For example, if nextUnusedDiscSquareForColumn[2] = 3, that means that for the third column (which is at index 2), the next available disc square is the fourth (not the third since arrays start at zero) from the bottom
    // 0 starts at the bottom of the board
    private int nextUnusedDiscForColumn[];

    // A string to specify disc color (could see some change in the future, but for now, its a start)
    private String discColor;

    // The number of rows and columns in the grid
    private int numColumns;
    private int numRows;

    // Constructors
    public Board() {
        this.discSquareArr = null;
        this.nextUnusedDiscForColumn = null;
        this.numColumns = 0;
        this.numRows = 0;
        this.discColor = "black";
    }
    public Board(String size) {

        // Set Disc Board Color
        this.discColor = "black";

        // Allocate disc array (boolean indicates if disc square is active or inactive)
        if (size.equalsIgnoreCase("7x6")) {
            this.discSquareArr = new boolean[7][6];
            this.numColumns = 7;
        } else if (size.equalsIgnoreCase("8x7")) {
            this.discSquareArr = new boolean[8][7];
            this.numColumns = 8;
        } else if (size.equalsIgnoreCase("10x8")) {
            this.discSquareArr = new boolean[10][8];
            this.numColumns = 10;
        }
        // If no valid size is given, we can't allocate anything
        // It might be worth trying to throw an exception in the future, though I don't see this case happening very often
        else {
            this.discSquareArr = null;
            return;
        }

        // Initialize nextUnusedDiscForColumn array (the number in the array indicates the next index of the column array from which there is an inactive--therefore unused--disc square)
        this.nextUnusedDiscForColumn = new int[numColumns];

        // Initialize disc array
        for (int i = 0; i < numColumns; ++i) {
            this.nextUnusedDiscForColumn[i] = 0;
            for (int j = 0; j < this.discSquareArr[i].length; ++j) {
                this.discSquareArr[i][j] = false;
            }
        }
    }

    /// Board Functions
    // Add a disc to a specified column, if possible.
    // INPUT: column (the column at which you wish to insert a disc)
    // OUTPUT: true if the disc was added successfully. False if it wasn't (if the column number was too large, for example)
    /*public boolean AddDiscToColumn(int column) {

        int nextUnusedDiscSpaceInColumn = 0;

        // Checks for the next available disc space in the desired column using the nextUnusedDiscForColumn array
        if (column > -1 && column < this.numColumns) {
            if (nextUnusedDiscForColumn[column] < numRows) {
                // This looked complicated, but basically, I am getting the next available unused disc in the specified column
                // And setting it as the second index variable in the discSquareArr call
                // I will also increment the nextUnusedDiscForColumn
                discSquareArr[column][nextUnusedDiscForColumn[column]] = true;
                ++(nextUnusedDiscForColumn[column]);

                // Trigger Animation

                return true;
            }
            // If the index of the next unused disc in the column is equal to the number of rows in the board, there is no more space left in the column
            // Therefore, we can't add to it
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    */
}
