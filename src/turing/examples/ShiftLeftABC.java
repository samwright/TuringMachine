package turing.examples;


import turing.*;


/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 18:33
 */
public class ShiftLeftABC {
    private static Machine m = new MachineImpl();

    static {
        m.addInstruction("s", '>', "s", Action.MOVERIGHT);
        m.addInstruction("s", '*', "moved right", Action.MOVERIGHT);
        m.addInstruction("moved right", ' ', "found empty", Action.MOVELEFT);
        m.addInstruction("found empty", '*', "h", ' ');

        m.addInstruction("wrote something", '*', "s", Action.MOVERIGHT);

        m.addInstruction("moved right", 'a', "found a", Action.MOVELEFT);
        m.addInstruction("found a", '*', "wrote something", 'a');
        m.addInstruction("moved right", 'b', "found b", Action.MOVELEFT);
        m.addInstruction("found b", '*', "wrote something", 'b');
        m.addInstruction("moved right", 'c', "found c", Action.MOVELEFT);
        m.addInstruction("found c", '*', "wrote something", 'c');

    }

    public static String run(String tape_str) {
        m.setTape(tape_str);
        m.resetState();
        m.run();
        return m.getTape().trim();
    }
}
