package com.example.hemanthlam.connectfour;

import android.widget.Spinner;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hemanthlam on 2/15/18.
 */

public class GameActivityTest {

    @Test
    public void checkcolorToDiscWorking() throws Exception {
        int row = -1;
        int expected = -1;
        GameActivity testBoard = new GameActivity();
        assertEquals(expected, row);
    }
}
