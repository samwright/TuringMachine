package turing.examples;

import turing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 25/11/2012
 * Time: 00:02
 */
public class CopyAfterGap implements Operation {
    private Machine m = new MachineImpl();

    public CopyAfterGap(Language language) {
        m.setVerbose(true);
        m.addInstruction("s", '>', "s", Action.MOVERIGHT);
        m.addInstruction("s", ' ', "s", Action.MOVERIGHT);
        m.addInstruction("s", '*', "found start", Action.MOVELEFT);
        m.addInstruction("found start", '*', "detect symbol", Action.MOVERIGHT);


        m.addInstruction("rewritten original", '*', "detect symbol", Action.MOVERIGHT);

        m.addInstruction("detect symbol", ' ', "h", ' ');

        for (char symbol : language.get()) {
            m.addInstruction("detect symbol", symbol, "deleting " + symbol, ' ');
            m.addInstruction("deleting " + symbol, ' ', "found " + symbol, Action.MOVERIGHT);
            m.addInstruction("found " + symbol, '*', "found " + symbol, Action.MOVERIGHT);
            m.addInstruction("found " + symbol, ' ', "skipping copy to write " + symbol, Action.MOVERIGHT);
            m.addInstruction("skipping copy to write " + symbol, '*', "skipping copy to write " + symbol, Action.MOVERIGHT);
            m.addInstruction("skipping copy to write " + symbol, ' ', "written " + symbol, symbol);

            m.addInstruction("written " + symbol, '*', "written " + symbol, Action.MOVELEFT);
            m.addInstruction("written " + symbol, ' ', "skipping original to rewrite " + symbol, Action.MOVELEFT);
            m.addInstruction("skipping original to rewrite " + symbol, '*', "skipping original to rewrite " + symbol, Action.MOVELEFT);
            m.addInstruction("skipping original to rewrite " + symbol, ' ', "rewritten original", symbol);

        }
    }

    public void run(Tape tape) {
        m.setTape(tape);
        m.resetState();
        m.run();
    }
}
