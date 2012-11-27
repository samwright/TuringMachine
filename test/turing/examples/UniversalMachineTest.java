package turing.examples;

import org.junit.Before;
import org.junit.Test;
import turing.*;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 27/11/2012
 * Time: 21:24
 */
public class UniversalMachineTest {
    private UniversalMachine universalMachine;
    private Machine m;
    private String input = ">";

    @Before
    public void setUp() {
        m = new MachineImpl();
        m.setTape(input);

        m.addInstruction("s", '>', "write 1", Action.MOVERIGHT);
        m.addInstruction("write 1", ' ', "written 1", '1');
        m.addInstruction("written 1", '1', "write 2", Action.MOVERIGHT);
        m.addInstruction("write 2", ' ', "written 2", '2');
        m.addInstruction("written 2", '2', "write 3", Action.MOVERIGHT);
        m.addInstruction("write 3", ' ', "h", '3');

    }

    @Test
    public void testRun() throws Exception {
        Language language = new LanguageImpl(new char[]{'1', '2', '3', ' ', '>'});
        universalMachine = new UniversalMachine(language);
        String instructions = m.getInstructions(language);

        m.run();
        assertEquals(">123", m.getTape());

        assertEquals(">123", universalMachine.run(input, instructions));
    }

    @Test
    public void testCopy() throws Exception {
        Machine combined = new MachineImpl();
        Language language = new LanguageImpl(new char[]{'1', '2', '3', '4', '5', ' ', '>'});
        universalMachine = new UniversalMachine(language);
        String input = ">12345";

        combined.appendMachine(new CopyAfterGap(language).getMachine());
        combined.appendMachine(new ShiftLeft(language).getMachine());

        combined.setTape(input);
        combined.run();
        assertEquals(">1234512345", combined.getTape().trim());

        //universalMachine.getMachine().setVerbose(true);
        String instructions = combined.getInstructions(language);
        assertEquals(">1234512345", universalMachine.run(input, instructions).trim());
    }
}
