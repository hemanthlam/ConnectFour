package com.example.hemanthlam.connectfour;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Sean on 1/30/2018.
 */
public class BoardTest {
    @Test
    public void findPosition() throws Exception {
        int row;
        int expected = 5;
        Board testBoard = new Board("7x6");
        row = testBoard.findPosition(0);
        assertEquals(row, expected);
        expected = -2;
        row = testBoard.findPosition(-10);
        assertEquals(row,expected);
        row = testBoard.findPosition(100);
        assertEquals(row,expected);
    }

}