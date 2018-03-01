package com.example.hemanthlam.connectfour;

import android.widget.Spinner;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hemanthlam on 2/15/18.
 */

public class Game1ActivityTest {

    @Test
    public void checkSampleWinner() throws Exception {
        int row;
        int expected = 0;
        Game1Activity testBoard = new Game1Activity();
        row = testBoard.p1Wins;
        assertEquals(expected, row);
    }
}
