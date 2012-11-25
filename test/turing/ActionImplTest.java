package turing;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 15:49
 */
public class ActionImplTest {
    private Action action;
    private String new_state = "write a";
    private char task = 'a';

    @Before
    public void setUp() throws Exception {
        action = new ActionImpl(new_state, task);
    }

    @Test
    public void testGetNewState() throws Exception {
        assertEquals(new_state, action.getNewState());
    }

    @Test
    public void testIsMoveRight() throws Exception {
        assertFalse(action.isMoveRight());
        assertTrue((new ActionImpl("move right", '>').isMoveRight()));
    }

    @Test
    public void testIsMoveLeft() throws Exception {
        assertFalse(action.isMoveLeft());
        assertTrue((new ActionImpl("move left", '<').isMoveLeft()));
    }

    @Test
    public void testGetTask() throws Exception {
        assertEquals(task, action.getTask());
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(action.equals(new ActionImpl(new_state, task)));
        assertTrue(action.equals(new ActionImpl("write a", 'a')));
    }

    @Test
    public void testNotEquals() throws Exception {
        assertFalse(action.equals(new ActionImpl("write b", 'a')));
        assertFalse(action.equals(new ActionImpl("write a", 'b')));
        assertFalse(action.equals(new ActionImpl("write b", 'b')));
    }

}
