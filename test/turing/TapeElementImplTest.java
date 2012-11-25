package turing;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 12:59
 */
public class TapeElementImplTest {
    private TapeElement e, e_prev, e_next;

    @Before
    public void setUp() throws Exception {
        e_next = new TapeElementImpl('b', null);
        e = new TapeElementImpl('a', e_next);
        e_prev = new TapeElementImpl('>', e);
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals('a', e.getValue());
    }

    @Test
    public void testSetValue() throws Exception {
        e.setValue('b');
        assertEquals('b', e.getValue());
    }

    @Test
    public void testGetNext() throws Exception {
        assertEquals(e, e_prev.getNext());
        assertEquals(e_next, e.getNext());
    }

    @Test
    public void testGetNonExistent() throws Exception {
        assertEquals(null, e_prev.getPrev());
        assertEquals(null, e_next.getNext());
    }

    @Test
    public void testGetPrev() throws Exception {
        assertEquals(e_prev, e.getPrev());
        assertEquals(e, e_next.getPrev());
    }

}
