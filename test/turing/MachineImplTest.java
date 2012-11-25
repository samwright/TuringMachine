package turing;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 15:25
 */
public class MachineImplTest {
    private Machine m;

    @Before
    public void setUp() throws Exception {
        m = new MachineImpl();
        m.setTape(">");

        m.addInstruction("s", '>', "write a", Action.MOVERIGHT);
        m.addInstruction("write a", ' ', "written a", 'a');
        m.addInstruction("written a", 'a', "write b", Action.MOVERIGHT);
        m.addInstruction("write b", ' ', "written b", 'b');
        m.addInstruction("written b", 'b', "write c", Action.MOVERIGHT);
        m.addInstruction("write c", ' ', "h", 'c');
    }

    @Test
    public void testAddInstruction() throws Exception {

    }

    @Test
    public void testGetAction() throws Exception {
        assertNotNull(m.getAction("s", '>'));
        assertTrue(m.getAction("s", '>').equals(new ActionImpl("write a", Action.MOVERIGHT)));
        assertNull(m.getAction("s", 'a'));
    }

    @Test
    public void testGetActionWildcardSymbol() throws Exception {
        m.addInstruction("foo", '*', "h", Action.MOVERIGHT);
        assertTrue(new ActionImpl("h", Action.MOVERIGHT).equals(m.getAction("foo", 'y')));

        m.addInstruction("foo", 'z', "h", 'z');
        checkActionWildcardSymbol();
    }

    private void checkActionWildcardSymbol() throws Exception {
        assertTrue(new ActionImpl("h", 'z').equals(m.getAction("foo", 'z')));
        testRunToCompletion();
    }

    @Test
    public void testGetActionWildcardState() throws Exception {
        m.addInstruction("*", 'z', "h", Action.MOVERIGHT);
        assertTrue(new ActionImpl("h", Action.MOVERIGHT).equals(m.getAction("bar", 'z')));

        m.addInstruction("bar", 'z', "h", 'z');
        checkActionWildcardState();
    }

    private void checkActionWildcardState() throws Exception {
        assertTrue(new ActionImpl("h", 'z').equals(m.getAction("bar", 'z')));
        testRunToCompletion();
    }

    @Test
    public void testGetActionDefault() throws Exception {
        m.addInstruction("*", '*', "default", Action.MOVERIGHT);
        checkActionDefault();
    }

    private void checkActionDefault() throws Exception {
        assertTrue(new ActionImpl("default", Action.MOVERIGHT).equals(m.getAction("foo", 'z')));
        assertTrue(new ActionImpl("default", Action.MOVERIGHT).equals(m.getAction("foo", 'y')));
        assertTrue(new ActionImpl("default", Action.MOVERIGHT).equals(m.getAction("bar", 'z')));
        assertTrue(new ActionImpl("default", Action.MOVERIGHT).equals(m.getAction("bar", 'y')));
        testRunToCompletion();
    }

    @Test
    public void testGetAction_WildcardStateBeforeDefault() throws Exception {
        testGetActionDefault();
        testGetActionWildcardState();
        try {
            checkActionDefault();
            fail("Default took precedence over wildcard state.");
        } catch (AssertionError err) {}
    }

    @Test
    public void testGetAction_WildcardSymbolBeforeDefault() throws Exception {
        testGetActionDefault();
        testGetActionWildcardSymbol();
        try {
            checkActionDefault();
            fail("Default took precedence over wildcard symbol.");
        } catch (AssertionError err) {}
    }

    @Test
    public void testGetAction_WildcardStateBeforeWildcardSymbol() throws Exception {
        testGetActionWildcardSymbol();
        testGetActionWildcardState();
        try {
            checkActionWildcardSymbol();
            fail("Wildcard symbol took precedence over wildcard state.");
        } catch (AssertionError err) {
        }
    }

    @Test
    public void testRunToCompletion() throws Exception {
        m.run();
        assertEquals(">abc", m.getTape());
    }

    @Test
    public void testRunOneAtATime() throws Exception {
        m.run(1);
        assertEquals("> ", m.getTape());
        assertFalse(m.isHalted());
        m.run(1);
        assertEquals(">a", m.getTape());
        assertFalse(m.isHalted());

        m.run(1);
        assertEquals(">a ", m.getTape());
        assertFalse(m.isHalted());
        m.run(1);
        assertEquals(">ab", m.getTape());
        assertFalse(m.isHalted());

        m.run(1);
        assertEquals(">ab ", m.getTape());
        assertFalse(m.isHalted());
        m.run(1);
        assertEquals(">abc", m.getTape());

        for (int i=0; i<10; ++i) {
            assertTrue(m.isHalted());
        }
    }

    @Test
    public void testResetMachine() throws Exception {
        m.run(1); // This doesn't change the tape, just the state and head pos.
        m.resetState();
        m.resetHead();
        testRunOneAtATime();
    }

    @Test
    public void testResetTape() throws Exception {
        testRunOneAtATime();
        m.resetTape();
        m.resetState();
        m.resetHead();
        testRunToCompletion();
    }

    @Test
    public void testGetTape() throws Exception {
        assertEquals(">", m.getTape());
    }

    @Test
    public void testSetTape() throws Exception {
        m.run();
        assertEquals(">abc", m.getTape());
        m.setTape(">");
        m.resetState();
        m.run();
        assertEquals(">abc", m.getTape());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(
                "(s, >)\n" +
                "    ^", m.toString());
        m.run(1);
        assertEquals(
                "(write a, > )\n" +
                "           ^", m.toString());

        m.run(1);
        assertEquals(
                "(written a, >a)\n" +
                "             ^", m.toString());

        m.run(1);
        assertEquals(
                "(write b, >a )\n" +
                "            ^", m.toString());

        m.run(1);
        assertEquals(
                "(written b, >ab)\n" +
                "              ^", m.toString());
    }

}
