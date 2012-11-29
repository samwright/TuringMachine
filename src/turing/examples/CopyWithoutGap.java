package turing.examples;

import turing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 25/11/2012
 * Time: 01:00
 */
public class CopyWithoutGap implements Operation {
    private Machine m = new MachineImpl();

    public CopyWithoutGap(Language language) {
        m.appendMachine(new CopyAfterGap(language).getMachine());
        m.appendMachine(new ShiftLeft(language).getMachine());

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
