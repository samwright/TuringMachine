package turing.examples;

import turing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 22:51
 */
public class ShiftLeft implements Operation {
    Machine m = new MachineImpl();

    public ShiftLeft(Language language) {
        m.addInstruction("s", '>', "s", Action.MOVERIGHT);
        m.addInstruction("s", '*', "moved right", Action.MOVERIGHT);
        m.addInstruction("moved right", ' ', "found empty", Action.MOVELEFT);
        m.addInstruction("found empty", '*', "h", ' ');

        m.addInstruction("wrote something", '*', "s", Action.MOVERIGHT);

        for (char symbol : language.get()) {
            if (symbol != ' ') {
                m.addInstruction("moved right", symbol, "found " + symbol, Action.MOVELEFT);
                m.addInstruction("found " + symbol, '*', "wrote something", symbol);
            }
        }
    }

    public void run(Tape tape) {
        m.setTape(tape);
        m.resetState();
        m.run();
    }

    public Machine getMachine() {
        return m;
    }
}
