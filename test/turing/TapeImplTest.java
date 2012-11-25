package turing;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 00:08
 */
public class TapeImplTest {
    private Tape tape;

    @Before
    public void setUp() throws Exception {
        tape = new TapeImpl(">abc");
    }

    @Test
    public void testEmptyInput() throws Exception {
        try {
            tape = new TapeImpl("");
            fail("Created tape with empty input");
        } catch (RuntimeException err) {
        }
    }

    @Test
    public void testBadInput() throws Exception {
        try {
            tape = new TapeImpl("abc");
            fail("Created tape with input \'abc\'");
        } catch (RuntimeException err) {
        }
    }

    @Test
    public void testWrite() throws Exception {
        tape.write('z');
        assertEquals('z', tape.read());

        tape.write(' ');
        assertEquals(' ', tape.read());
    }

    @Test
    public void testMove() throws Exception {
        assertEquals('>', tape.read());

        tape.moveRight();
        assertEquals('a', tape.read());

        tape.moveRight();
        assertEquals('b', tape.read());

        tape.moveRight();
        assertEquals('c', tape.read());
    }

    @Test
    public void testMoveOutOfBoundLeft() throws Exception {
        try {
            tape.moveLeft();
            fail("Was able to move left of > and read " + tape.read());
        } catch (RuntimeException err) {
        }

        assertEquals('>', tape.read());

    }

    @Test
    public void testMoveOutOfBoundRight() throws Exception {
        for (int i = 0; i < 4; ++i) {
            tape.moveRight();
        }

        for (int i = 0; i < 10; ++i) {
            assertEquals(' ', tape.read());
            tape.moveRight();
        }

    }

    @Test
    public void testRead() throws Exception {
        assertEquals('>', tape.read());
    }

    @Test
    public void testReset() throws Exception {
        tape.moveRight();
        assertEquals('a', tape.read());

        tape.reset();
        assertEquals('>', tape.read());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(">abc", tape.toString());
    }

    @Test
    public void testGetPosition() throws Exception {
        assertEquals(0, tape.getPosition());

        for (int i=1; i<=4; ++i) {
            tape.moveRight();
            assertEquals(i, tape.getPosition());
        }

        for (int i=3; i>=0; --i) {
            tape.moveLeft();
            assertEquals(i, tape.getPosition());
        }

        try {
            tape.moveLeft();
            fail("Was able to move left of the start");
        } catch (RuntimeException err) {}
        assertEquals(0, tape.getPosition());
    }
}
