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
<<<<<<< HEAD
        int row = -1;
        int expected = -1;
        GameActivity testBoard = new GameActivity();
=======
        int row;
        int expected = -1;
        GameActivity testBoard = new GameActivity();
        row = testBoard.colorToDiscImgId(null);
>>>>>>> 649658b22f57afdabe21e6eeafbe43aba38b8723
        assertEquals(expected, row);
    }
}
