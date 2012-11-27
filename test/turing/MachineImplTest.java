package turing;

import org.junit.Before;
import org.junit.Test;
import turing.examples.CopyAfterGap;
import turing.examples.ShiftLeft;

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

    @Test
    public void testGetInstructions() {
        String expected = "(a10,a,a110,»)(a1,>,a101,»)(a11,b,a100,»)(a100, ,a0,c)(a101, ,a10,a)(a110, ,a11,b)(a0, ,a0,h,b,a0,h,c,a0,h,a,a0,h,>,a0,h)";
        String received = m.getInstructions(new LanguageImpl(new char[]{'a', 'b', 'c', ' ', '>'}));
        assertEquals(expected, received);
    }

    @Test
    public void testGetTwoSymbolsInstructions() {
        m.addInstruction("written b", 'c', "h", Action.MOVERIGHT);
        String expected = "(a10,a,a110,»)(a1,>,a101,»)(a11,b,a100,»,c,a0,»)(a100, ,a0,c)(a101, ,a10,a)(a110, ,a11,b)(a0, ,a0,h,b,a0,h,c,a0,h,a,a0,h,>,a0,h)";
        String received = m.getInstructions(new LanguageImpl(new char[]{'a', 'b', 'c', ' ', '>'}));
        assertEquals(expected, received);
    }

    @Test
    public void testGetWildcardSymbolInstructions() {
        m.addInstruction("write c", '*', "h", Action.MOVERIGHT);
        String expected = "(a10,a,a110,»)(a1,>,a101,»)(a11,b,a100,»)(a100, ,a0,c,b,a0,»,c,a0,»,a,a0,»,>,a0,»)(a101, ,a10,a)(a110, ,a11,b)(a0, ,a0,h,b,a0,h,c,a0,h,a,a0,h,>,a0,h)";
        String received = m.getInstructions(new LanguageImpl(new char[]{'a', 'b', 'c', ' ', '>'}));
        assertEquals(expected, received);
    }

    @Test
    public void testGetWithNumbers() {
        m = new MachineImpl();
        m.setTape(">");

        m.addInstruction("s", '>', "write a", Action.MOVERIGHT);
        m.addInstruction("write a", ' ', "written a", '1');
        m.addInstruction("written a", '1', "write b", Action.MOVERIGHT);
        m.addInstruction("write b", ' ', "written b", '2');
        m.addInstruction("written b", '2', "write c", Action.MOVERIGHT);
        m.addInstruction("write c", ' ', "h", '3');


        String expected = "(a10,1,a110,»)(a1,>,a101,»)(a11,2,a100,»)(a100, ,a0,3)(a101, ,a10,1)(a110, ,a11,2)(a0,3,a0,h,2,a0,h, ,a0,h,1,a0,h,>,a0,h)";
        String received = m.getInstructions(new LanguageImpl(new char[]{'1', '2', '3', ' ', '>'}));
        assertEquals(expected, received);
    }

    @Test
    public void testAppendMachine() {
        Machine add_another_start = new MachineImpl();
        add_another_start.addInstruction("s", 'c', "write >", Action.MOVERIGHT);
        add_another_start.addInstruction("write >", ' ', "h", '>');

        Machine n = new MachineImpl();
        n.setTape(">");

        n.addInstruction("s", '>', "write a", Action.MOVERIGHT);
        n.addInstruction("write a", ' ', "written a", '1');
        n.addInstruction("written a", '1', "write b", Action.MOVERIGHT);
        n.addInstruction("write b", ' ', "written b", '2');
        n.addInstruction("written b", '2', "write c", Action.MOVERIGHT);
        n.addInstruction("write c", ' ', "h", '3');

        Machine combined_machine = new MachineImpl();

        combined_machine.appendMachine(m);
        combined_machine.appendMachine(add_another_start);
        combined_machine.appendMachine(n);

        combined_machine.setTape(">");
        combined_machine.run();
        assertEquals(">abc>123", combined_machine.getTape());

    }

    @Test
    public void testAppendMachineSelf() {
        Machine add_another_start = new MachineImpl();
        add_another_start.addInstruction(Machine.START, 'c', "write >", Action.MOVERIGHT);
        add_another_start.addInstruction("write >", ' ', Machine.HALT, '>');

        m.appendMachine(add_another_start);
        m.appendMachine(m);

        m.run();
        assertEquals(">abc>abc>", m.getTape());

    }

    @Test
    public void testAppendingToCopy() {
        Machine combined = new MachineImpl();
        Language language = new LanguageImpl(new char[]{'1','2','3','4','5',' ','>'});

        combined.appendMachine(new CopyAfterGap(language).getMachine());
        combined.appendMachine(new ShiftLeft(language).getMachine());

        combined.setTape(">12345");
        combined.run();
        assertEquals(">1234512345", combined.getTape().trim());
    }

}
