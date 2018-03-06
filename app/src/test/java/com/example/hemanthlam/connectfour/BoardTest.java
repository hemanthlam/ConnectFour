package com.example.hemanthlam.connectfour;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Hemanth on 2/15/2018.
 */
public class BoardTest {

    @Test
    public void findPosition() throws Exception {
        int row;
        int expected = 5;
        Board testBoard1 = new Board("7x6");
        row = testBoard1.findPosition(0,0);
        assertEquals(expected, row);
        expected = -2;
        row = testBoard1.findPosition(-10, 0);
        assertEquals(expected, row);
        row = testBoard1.findPosition(100, 0);
        assertEquals(expected, row);

        Board testBoard2 = new Board("8x7");
        expected = 6;
        row = testBoard2.findPosition(0,0);
        assertEquals(expected, row);
        expected = -2;
        row = testBoard2.findPosition(-10, 0);
        assertEquals(expected, row);

        Board testBoard3 = new Board("10x8");
        row = testBoard3.findPosition(0,0);
        expected = 7;
        assertEquals(expected, row);
        expected = -2;
        row = testBoard3.findPosition(-10, 0);
        assertEquals(expected, row);
    }

    @Test
    public void checkingVerticalPosition() throws Exception {
        int[][] i;
        String expected =null;
        Board board1 = new Board(("7x6"));
        i = board1.checkVertical(1);
        assertEquals(expected, i);

        Board board2 = new Board(("8x7"));
        i = board2.checkVertical(1);
        assertEquals(expected, i);

        Board board3 = new Board(("10x8"));
        i = board3.checkVertical(1);
        assertEquals(expected, i);
    }

    @Test
    public void checkingHorizontalPosition() throws Exception {
        int[][] i;
        String expected =null;
        Board board1 = new Board(("7x6"));
        i = board1.checkHorizontal(1);
        assertEquals(expected, i);

        Board board2 = new Board(("8x7"));
        i = board2.checkHorizontal(1);
        assertEquals(expected, i);

        Board board3 = new Board(("10x8"));
        i = board3.checkHorizontal(1);
        assertEquals(expected, i);
    }

    @Test
    public void checkingDiagonalPosition() throws Exception {
        int[][] i;
        String expected =null;
        Board board1 = new Board(("7x6"));
        i = board1.checkDiagonal(1);
        assertEquals(expected, i);

        Board board2 = new Board(("8x7"));
        i = board2.checkDiagonal(2);
        assertEquals(expected, i);

        Board board3 = new Board(("10x8"));
        i = board3.checkDiagonal(2);
        assertEquals(expected, i);
    }

    @Test
    public void TestingtofindWinner() throws Exception {
        int[][] i;
        String expected =null;
        Board board1 = new Board(("7x6"));
        for(int j = 0; j < 7;++j){
            for(int k = 0; k < 6; ++k)
                board1.findPosition(j, 1);
        }
        i = board1.findWinner(1);
        assertNotNull(expected, i);
        i = board1.findWinner(2);
        assertEquals(expected, i);

        Board board2 = new Board(("8x7"));
        for(int j = 0; j < 8;++j){
            for(int k = 0; k < 7; ++k)
                board2.findPosition(j, 1);
        }
        i = board2.findWinner(1);
        assertNotNull(expected, i);
        i = board2.findWinner(2);
        assertEquals(expected, i);

        Board board3 = new Board(("10x8"));
        for(int j = 0; j < 10;++j){
            for(int k = 0; k < 8; ++k)
                board3.findPosition(j, 1);
        }
        i = board3.findWinner(1);
        assertNotNull(i);
        i = board3.findWinner(2);
        assertEquals(expected, i);

    }

    @Test
    public void TestingifBoardisFull() throws Exception {
        boolean i;
        boolean expected = false;
        Board board1 = new Board(("7x6"));
        i = board1.checkIfBoardFull();
        assertEquals(expected, i);
        for(int j = 0; j < 7;++j){
            for(int k = 0; k < 6; ++k)
                board1.findPosition(j, 1);
        }
        assertEquals(true, board1.checkIfBoardFull());
    }

}