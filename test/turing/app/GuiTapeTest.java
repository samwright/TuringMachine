package turing.app;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 29/11/2012
 * Time: 00:31
 */
public class GuiTapeTest {
    GuiTape tape;

    @Before
    public void setUp() {
        tape = new GuiTape(">abcdefghijklmno");
        for (int i=0; i<10; ++i) {
            tape.moveRight();
        }
        tape.setOutputWidth(5);
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("", tape.toString());
    }
}
