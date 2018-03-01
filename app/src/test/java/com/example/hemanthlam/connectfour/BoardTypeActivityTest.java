package com.example.hemanthlam.connectfour;


import android.widget.Spinner;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hemanthlam on 2/15/18.
 */

public class BoardTypeActivityTest {

    @Test
    public void checkGameSpinner() throws Exception {
        Spinner row;
        String expected = null;
        BoardTypeActivity testBoard = new BoardTypeActivity();
        row = testBoard.gameSpinner;
        assertEquals(expected, row);
    }

}
