package turing.examples;

import turing.Machine;
import turing.MachineImpl;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 18:33
 */
public class ShiftLeftABC {
    private static Machine m = new MachineImpl();

    static {
        m.addInstruction("s", '>', "s", '>');
        m.addInstruction("s", '*', "moved right", '>');
        m.addInstruction("moved right", ' ', "found empty", '<');
        m.addInstruction("found empty", '*', "h", ' ');

        m.addInstruction("wrote something", '*', "s", '>');

        m.addInstruction("moved right", 'a', "found a", '<');
        m.addInstruction("found a", '*', "wrote something", 'a');
        m.addInstruction("moved right", 'b', "found b", '<');
        m.addInstruction("found b", '*', "wrote something", 'b');
        m.addInstruction("moved right", 'c', "found c", '<');
        m.addInstruction("found c", '*', "wrote something", 'c');

    }

    public static String run(String tape_str) {
        m.setTape(tape_str);
        m.resetState();
        m.run();
        return m.getTape().trim();
    }
}
