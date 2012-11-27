package turing.examples;

import turing.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 25/11/2012
 * Time: 06:56
 *
 * ">'state''symbol''new_state''task''symbol''new_state''task';; >abc..."
 *                                                              ^ buffer
 *                                                               (holds current symbol,
 *                                                               replaced by 'a').
 *  state 2 = " a 1 0" (ie. a then the number in binary, with gaps)
 *  symbol T = "bT" (ie. b then the symbol)
 *  new_state 3 = " 1 1" (ie. the number in binary, with gaps)
 *  task = 'r' or 'l' for moving head, or 'w' then char to write char
 *
 *  All states are interspersed with spaces (ie. "a10" -> " a 1 0")
 *  so markers can be set.
 *
 *  eg. '123' writer:
 *  m.addInstruction("a1", '>', "a10", '1'); => a 1b> 1 0w1;
 *
 *  m.addInstruction("a10", '1', "a10", '>'); => a 1 0b1 1 0r;
 *  m.addInstruction("a10", ' ', "a11", '2'); => a 1 0b  1 1w2;
 *                                      => a 1 0b1 1 0rb  1 1w2;
 *
 *  m.addInstruction("a11", '2', "a11", '>'); => a 1 1b2 1 1r;
 *  m.addInstruction("a11", ' ', "a0", '3'); => a 1 1b  0w3;
 *                                      => a 1 1b2 1 1rb  0w3;
 *
 *  m.addInstruction("a0", '3', "a0", 'h'); => a 0b3 0h;
 *
 *  All in, is:
 *  > a 1b> 1 0w1; a 1 0b1 1 0rb  1 1w2; a 1 1b2 1 1rb  0w3; a 0b3 0h;
 *
 *  Now to complete the tape - add the input (here it is just ">"):
 *
 *  > a 1b> 1 0w1; a 1 0b1 1 0rb  1 1w2; a 1 1b2 1 1rb  0w3; a 0b3 0h;; >
 *
 *  and put the start symbol (>) in the buffer, replacing it with the current-
 *  position marker (p):
 *
 *  > a 1b> 1 0w1; a 1 0b1 1 0rb  1 1w2; a 1 1b2 1 1rb  0w3; a 0b3 0h;;>p
 *
 *  and put the this-state marker (t) on a1:
 *
 *  >ta 1b> 1 0w1; a 1 0b1 1 0rb  1 1w2; a 1 1b2 1 1rb  0w3; a 0b3 0h;;>p
 *
 *   (Where halt is a0, start is a1.)
 *
 */
public class UniversalExecutor implements Operation {
    private Machine m = new MachineImpl();

    public static final char START = '=';
    public static final char INNERSTART = '>';
    public static final char HALT = Machine.HALT.charAt(0);
    public static final char WRITE = 'w';

    // TODO: these chars aren't ever used in UniversalExecutor!  Changing them doesn't change the compilation language.
    public static final char STATE_START = 'a';
    public static final char SYMBOL_START = 'b';
    public static final char STATE_END = ';';
    public static final char ITER_I = 'i';
    public static final char ITER_J = 'j';
    public static final char BUFFER_POINTER = 'p';




    public UniversalExecutor(Language language){
        Language binary_language = new LanguageImpl(new char[]{'0', '1'});
        Language task_language = new LanguageImpl(new char[]{WRITE, Action.MOVELEFT, Action.MOVERIGHT, HALT});
        Set<Character> extended_language_set= new HashSet<Character>(language.get());
        extended_language_set.add(INNERSTART);
        extended_language_set.add(Tape.EMPTY);

        m.setVerbose(true);


        // Move to buffer (ie. one after ;;)
        m.addInstruction("s",START, "right to buffer", Action.MOVERIGHT);
        m.addInstruction("right to buffer", '*', "right to buffer", Action.MOVERIGHT);
        m.addInstruction("right to buffer", ';', "right to buffer, found ;", Action.MOVERIGHT);
        m.addInstruction("right to buffer, found ;", ';', "at buffer", Action.MOVERIGHT);
        m.addInstruction("right to buffer, found ;", '*', "right to buffer", Action.MOVERIGHT);

        for (char symbol : extended_language_set) {
            // Remember buffer in state, and move to this_state (t) (GENERALISE)
            m.addInstruction("at buffer", symbol, "left to symbol " + symbol, Action.MOVELEFT);
            m.addInstruction("left to symbol " + symbol, '*', "left to symbol " + symbol, Action.MOVELEFT);

            // remove this_state marker, then look for symbol >
            m.addInstruction("left to symbol " + symbol, 't', "right to symbol " + symbol, ' ');
            m.addInstruction("right to symbol " + symbol, '*', "right to symbol " + symbol, Action.MOVERIGHT);
            m.addInstruction("right to symbol " + symbol, 'b', "check for symbol " + symbol, Action.MOVERIGHT);
            m.addInstruction("check for symbol " + symbol, symbol, "found symbol", Action.MOVERIGHT);
            m.addInstruction("check for symbol " + symbol, '*', "right to symbol " + symbol, Action.MOVERIGHT);

            // Couldn't find symbol > in state a1
            m.addInstruction("check for symbol " + symbol, ';', "h", Action.MOVERIGHT);
        }

        // Now that we are in the right state and symbol section, we can set
        // the next state.  First, mark that this is the correct section with a 'i',
        // which will be the iterator symbol we'll use later to compare state numbers
        // (to find the next state).  ie. "b> 1 0" becomes "b>i1 0"
        m.addInstruction("found symbol", ' ', "returning to start to mark next state", 'i');

        // Now go to the far left, then go right looking for the 'j' iterator.  If one is found, then
        // that state has already been checked and is wrong, so find the state after that.
        m.addInstruction("returning to start to mark next state", '*', "returning to start to mark next state", Action.MOVELEFT);
        m.addInstruction("returning to start to mark next state", START, "searching for previously-checked state", Action.MOVERIGHT);
        m.addInstruction("searching for previously-checked state", '*', "searching for previously-checked state", Action.MOVERIGHT);
        m.addInstruction("searching for previously-checked state", 'j', "mark next state", ' ');

        // If no 'j' iterator is found, then this is the first time doing this loop!  Go to the start
        // and choose the first state to check.
        m.addInstruction("searching for previously-checked state", ';', "maybe finished searching for previously-checked state", Action.MOVERIGHT);
        m.addInstruction("maybe finished searching for previously-checked state", ';', "mark first state", Action.MOVELEFT);
        m.addInstruction("maybe finished searching for previously-checked state", '*', "searching for previously-checked state", Action.MOVERIGHT);
        m.addInstruction("mark first state", '*', "mark first state", Action.MOVELEFT);
        m.addInstruction("mark first state", START, "mark next state", Action.MOVERIGHT);

        // Regardless of how we got here, our task is now to mark the next state to be checked to
        // see if it should be the next state.  To do this, mark the first
        // bit of the state number with the iterator symbol 'j' (ie. "a 0 1" becomes "aj0 1")
        m.addInstruction("mark next state", '*', "mark next state", Action.MOVERIGHT);
        m.addInstruction("mark next state", 'a', "writing j iterator", Action.MOVERIGHT);
        m.addInstruction("writing j iterator", ' ', "writing j iterator", 'j');
        m.addInstruction("writing j iterator", 'j', "read iterator j", Action.MOVERIGHT);

        for (char digit : binary_language.get()) {
            m.addInstruction("read iterator j", digit, "iterator j = " + digit, Action.MOVELEFT);

            // Now return to far left, and search for the 'i' iterator.
            m.addInstruction("iterator j = " + digit, '*', "returning to start to find i (j=" + digit + ")", Action.MOVELEFT);
            m.addInstruction("returning to start to find i (j=" + digit + ")", START, "finding i (j=" + digit + ")", Action.MOVERIGHT);
            m.addInstruction("returning to start to find i (j=" + digit + ")", '*', "returning to start to find i (j=" + digit + ")", Action.MOVELEFT);
            m.addInstruction("finding i (j=" + digit + ")", '*', "finding i (j=" + digit + ")", Action.MOVERIGHT);
            m.addInstruction("finding i (j=" + digit + ")", 'i', "found i (j=" + digit + ")", Action.MOVERIGHT);

            // If the iterators match, continue until there are no more state digits to test.
            m.addInstruction("found i (j=" + digit + ")", digit, "iterators match, keep iterating", Action.MOVELEFT);
        }

        // If the iterators match, continue until there are no more state digits to test.
        m.addInstruction("iterators match, keep iterating", 'i', "iterators match, keep iterating", ' ');
        m.addInstruction("iterators match, keep iterating", ' ', "iterators match, keep iterating", Action.MOVERIGHT);
        for (char digit : binary_language.get()) {
            //m.addInstruction("iterators match, keep iterating", digit, "increment i", Action.MOVERIGHT);
            m.addInstruction("iterators match, keep iterating", digit, "increment i", Action.MOVERIGHT);
        }
        m.addInstruction("increment i", ' ', "returning to start to iterate j", 'i');

        // Now that i has been incremented (ie. "b>i1 0" becomes "b> 1i0") do the same to j:
        m.addInstruction("increment i", '*', "returning to start to iterate j", 'i');
        m.addInstruction("returning to start to iterate j", '*', "returning to start to iterate j", Action.MOVELEFT);
        m.addInstruction("returning to start to iterate j", START, "find and iterate j", Action.MOVERIGHT);
        m.addInstruction("find and iterate j", '*', "find and iterate j", Action.MOVERIGHT);

        // On finding j, delete it and put it after the next digit:
        // NB. this looks different to iterating i because the formatting is different.
        m.addInstruction("find and iterate j", 'j', "iterate j", ' ');
        m.addInstruction("iterate j", ' ', "iterate j", Action.MOVERIGHT);
        m.addInstruction("iterate j", '0', "mark iterator j", Action.MOVERIGHT);
        m.addInstruction("iterate j", '1', "mark iterator j", Action.MOVERIGHT);
        m.addInstruction("mark iterator j", ' ', "writing j iterator", 'j'); // This is where the check loops!!

        // But if j ends before i then the states aren't the same!  Restore j, then start the loop again.
        m.addInstruction("mark iterator j", 'b', "restore j", Action.MOVELEFT);
        m.addInstruction("restore j", '*', "restore j", Action.MOVELEFT);
        m.addInstruction("restore j", ' ', "iterators don't match, reset", 'j');

        // But if there are no more state digits at i
        for (char task : task_language.get()) {
            //m.addInstruction("iterators match, keep iterating", task, "restore i", Action.MOVELEFT);
            m.addInstruction("increment i", task, "restore i", Action.MOVELEFT);
        }

        // Then restore 'i' so we can find our place again
        m.addInstruction("restore i", '*', "restore i", Action.MOVELEFT);
        m.addInstruction("restore i", ' ', "returning to start to assert j finished", 'i');
        // then find j to make sure it also finished:
        m.addInstruction("returning to start to assert j finished", '*', "returning to start to assert j finished", Action.MOVELEFT);
        m.addInstruction("returning to start to assert j finished", START, "find j to assert finished", Action.MOVERIGHT);
        m.addInstruction("find j to assert finished", '*', "find j to assert finished", Action.MOVERIGHT);
        m.addInstruction("find j to assert finished", 'j', "assert j finished", Action.MOVERIGHT);
        for (char digit : binary_language.get()) {
            m.addInstruction("assert j finished", digit, "assert j finished", Action.MOVERIGHT);
        }

        // If j finished then this is the next state!! The procedure now is:
        //  - Move left and remove j
        //  - Move left and find the next 'a'; mark it as 't' (this_state)
        //  - Return to far left
        //  - Move right until 'i', then remove it.
        //  - Process the "task" of the instruction
        m.addInstruction("assert j finished", 'b', "next state found, remove j", Action.MOVELEFT);
        m.addInstruction("next state found, remove j", '*', "next state found, remove j", Action.MOVELEFT);
        m.addInstruction("next state found, remove j", 'j', "next state found, mark t", ' ');
        m.addInstruction("next state found, mark t", '*', "next state found, mark t", Action.MOVELEFT);
        m.addInstruction("next state found, mark t", 'a', "mark t", Action.MOVELEFT);
        m.addInstruction("mark t", ' ', "returning to start to complete instruction", 't');

        m.addInstruction("returning to start to complete instruction", '*', "returning to start to complete instruction", Action.MOVELEFT);
        m.addInstruction("returning to start to complete instruction", START, "complete instruction", Action.MOVERIGHT);
        m.addInstruction("complete instruction", '*', "complete instruction", Action.MOVERIGHT);

        m.addInstruction("complete instruction", 'i', "skip number to get to job", ' ');
        m.addInstruction("skip number to get to job", '*', "skip number to get to job", Action.MOVERIGHT);

        // But if the iterators (ie. the states) don't match:
        m.addInstruction("found i (j=0)", '1', "iterators don't match, reset", Action.MOVELEFT);
        m.addInstruction("found i (j=1)", '0', "iterators don't match, reset", Action.MOVELEFT);
        // Then:
        //  - Return to start, find i, reset it
        //  - form loop, looking for the next unchecked state
        m.addInstruction("iterators don't match, reset", '*', "iterators don't match, reset", Action.MOVELEFT);
        m.addInstruction("iterators don't match, reset", START, "reset i", Action.MOVERIGHT);
        m.addInstruction("reset i", '*', "reset i", Action.MOVERIGHT);
        m.addInstruction("reset i", 'i', "deleted i", ' ');

        // Form loop which will recreate i and keep looking for a matching state:
        // Look left for 'b', then go 2 right and write i
        m.addInstruction("deleted i", '*', "deleted i", Action.MOVELEFT);
        m.addInstruction("deleted i", 'b', "nearly ready to recreate i", Action.MOVERIGHT);

        m.addInstruction("nearly ready to recreate i", '*', "recreate i", Action.MOVERIGHT);
        m.addInstruction("recreate i", '*', "returning to start to mark next state", 'i');  // This is the loop!




        // Decide which task to do
        for (char task : task_language.get()) {

            m.addInstruction("skip number to get to job", task, "job is: " + task, Action.MOVERIGHT);
            if (task != WRITE) {
                m.addInstruction("job is: " + task, '*', "job is: " + task, Action.MOVERIGHT);
                m.addInstruction("job is: " + task, ';', "maybe at end of functions, job is: " + task, Action.MOVERIGHT);
                m.addInstruction("maybe at end of functions, job is: " + task, '*', "job is: " + task, Action.MOVERIGHT);
                m.addInstruction("maybe at end of functions, job is: " + task, ';', "reading buffer, job is: " + task, Action.MOVERIGHT);

                if (task == Action.MOVELEFT)
                    m.addInstruction("buffer popped, job is:" + task, '*', "read new buffer", Action.MOVELEFT);
                else if (task == Action.MOVERIGHT)
                    m.addInstruction("buffer popped, job is:" + task, '*', "read new buffer", Action.MOVERIGHT);
                else if (task == HALT)
                    m.addInstruction("buffer popped, job is:" + task, '*', "h", Action.MOVERIGHT);
            }

            for (char symbol : extended_language_set) {
                if (task == WRITE) {
                    m.addInstruction("job is: " + task, symbol, "job is: " + task + " " + symbol, Action.MOVERIGHT);
                    m.addInstruction("job is: " + task + " " + symbol, '*', "job is: " + task + " " + symbol, Action.MOVERIGHT);
                    m.addInstruction("job is: " + task + " " + symbol, ';', "maybe at end of functions, job is: " + task + " " + symbol, Action.MOVERIGHT);
                    m.addInstruction("maybe at end of functions, job is: " + task + " " + symbol, '*', "job is: " + task + " " + symbol, Action.MOVERIGHT);
                    m.addInstruction("maybe at end of functions, job is: " + task + " " + symbol, ';', "writing buffer, job is: " + task + " " + symbol, Action.MOVERIGHT);
                    m.addInstruction("writing buffer, job is: " + task + " " + symbol, '*', "at buffer", symbol);
                } else {
                    // Replacing old buffer back into input tape
                    m.addInstruction("reading buffer, job is: " + task, symbol, "buffer is "+symbol+", now cleaning, job is:" + task, ' ');
                    m.addInstruction("buffer is " + symbol + ", now cleaning, job is:" + task, ' ', "buffer is " + symbol + ", job is:" + task, Action.MOVERIGHT);
                    m.addInstruction("buffer is " + symbol + ", job is:" + task, '*', "buffer is " + symbol + ", job is:" + task, Action.MOVERIGHT);
                    m.addInstruction("buffer is " + symbol + ", job is:" + task, 'p', "buffer popped, job is:" + task, symbol);

                    // Both l and r share these states, and we don't want duplicates.
                    if (task == Action.MOVELEFT) {
                        // Taking new value from input tape and storing in buffer
                        m.addInstruction("read new buffer", symbol, "new buffer is " + symbol, 'p');
                        m.addInstruction("new buffer is " + symbol, '*', "new buffer is " + symbol, Action.MOVELEFT);
                        m.addInstruction("new buffer is " + symbol, ';', "writing new buffer of " + symbol, Action.MOVERIGHT);
                        m.addInstruction("writing new buffer of " + symbol, '*', "at buffer", symbol);
                    }

                }
            }
        }

    }

    public void run(Tape tape) {
        m.setTape(tape);
        m.resetState();
        m.run();
    }
}
