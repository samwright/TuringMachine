package turing.examples;

import turing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 27/11/2012
 * Time: 15:22
 */
public class UniversalMachine implements UniversalOperation {
    private final Machine m = new MachineImpl();

    public UniversalMachine(Language language) {
        Machine compiler = new UniversalCompiler(language).getMachine();
        Machine executor = new UniversalExecutor(language).getMachine();

        m.appendMachine(compiler);
        m.appendMachine(executor);

    }

    @Override
    public Machine getMachine() {
        return m;
    }

    @Override
    public void run(Tape tape) {
        m.setTape(tape);
        m.resetState();
        m.run();
    }

    public String run(String input, String states) {
        Tape tape = new TapeImpl(input + states);
        run(tape);
        String[] outputs = tape.toString().split(";; *");
        return outputs[outputs.length-1].trim();
    }
}
