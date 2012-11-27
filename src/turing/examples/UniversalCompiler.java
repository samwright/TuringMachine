package turing.examples;

import turing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 26/11/2012
 * Time: 23:32
 *
 * " (1,a,1,»)(1,>,1,»)(1,b,1,»)(1, ,10,c)(1, ,1,a)(1, ,1,b)"
 *  (state,symbol,new_state,task)
 *
 *  becomes:
 *  " (1,a,1,»)(1,>,1,»)(1,b,1,»)(1, ,10,c)(1, ,1,a)(1, ,1,b)=ta 1b> 1 0w1; a 1 0b1 1 0»b  1 1w2; a 1 1b2 1 1»b  0w3; a 0b3 0h;;"
 *
 *  (note the ' ' represents the input.  Later on, the input to the compiled program goes there, and can't contain a ')',
 *  so the '(' not prefixed by a ')' is the beginning of the states.  For testing, '*' = ' '.
 *
 */
public class UniversalCompiler implements Operation {
    private Machine m = new MachineImpl();

    public static final char INST_START = '(';
    public static final char INST_END = ')';
    public static final char INST_DELIM = ',';

    public UniversalCompiler(Language language) {
        // We'll put a 'p' at the beginning, and a '=' at the end
        m.addInstruction("s", ' ', "going right to mark end", UniversalExecutor.BUFFER_POINTER);
        m.addInstruction("going right to mark end", '*', "going right to mark end", Action.MOVERIGHT);
        m.addInstruction("going right to mark end", ')', "going right to mark end - am I at the end?", Action.MOVERIGHT);
        m.addInstruction("going right to mark end - am I at the end?", '(', "going right to mark end", Action.MOVERIGHT);
        m.addInstruction("going right to mark end - am I at the end?", ' ', "mark writing iterator to right", UniversalExecutor.START);
        m.addInstruction("mark writing iterator to right", UniversalExecutor.START, "go left to mark reading iterator", UniversalExecutor.ITER_J);

        // The 'j' iterator is the marker we'll use to keep track of where to write next.
        // The 'i' iterator is put where we've read a value to move to j.
        m.addInstruction("go left to mark reading iterator", '*', "go left to mark reading iterator", Action.MOVELEFT);
        m.addInstruction("go left to mark reading iterator", UniversalExecutor.BUFFER_POINTER, "check for next state", Action.MOVERIGHT);

        // The "check for next state" is the start of the main loop!  If it sees a '(' it starts the reading/writing process.
        m.addInstruction("check for next state", INST_START, "mark reading iterator for state", Action.MOVERIGHT);


        // TODO: When there are no more states:
        m.addInstruction("check for next state", UniversalExecutor.START, "end of states", Action.MOVERIGHT);

    }

    public void run(Tape tape) {
        m.setTape(tape);
        m.resetState();
        m.run();
    }
}
