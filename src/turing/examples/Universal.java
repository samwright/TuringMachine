package turing.examples;

import turing.*;

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
 *  All in, is:
 *  > a 1b> 1 0w1; a 1 0b1 1 0rb  1 1w2; a 1 1b2 1 1rb  0w3;
 *
 *  Now to complete the tape - add the input (here it is just ">"):
 *
 *  > a 1b> 1 0w1; a 1 0b1 1 0rb  1 1w2; a 1 1b2 1 1rb  0w3;; >
 *
 *  and put the start symbol (>) in the buffer, replacing it with the current-
 *  position marker (p):
 *
 *  > a 1b> 1 0w1; a 1 0b1 1 0rb  1 1w2; a 1 1b2 1 1rb  0w3;;>p
 *
 *  and put the this-state marker (t) on a1:
 *
 *  >ta 1b> 1 0w1; a 1 0b1 1 0rb  1 1w2; a 1 1b2 1 1rb  0w3;;>p
 *
 *   (Where halt is a0, start is a1.)
 *
 */
public class Universal implements Operation {
    private Machine m = new MachineImpl();

    public Universal(Language language){
        Language binary_language = new LanguageImpl(new char[]{'0', '1'});
        Language task_language = new LanguageImpl(new char[]{'w', 'l', 'r'});

        // Move to buffer (ie. one after ;;)
        m.addInstruction("s",'>', "right to buffer", Action.MOVERIGHT);
        m.addInstruction("right to buffer", '*', "right to buffer", Action.MOVERIGHT);
        m.addInstruction("right to buffer", ';', "right to buffer, found ;", Action.MOVERIGHT);
        m.addInstruction("right to buffer, found ;", ';', "at buffer", Action.MOVERIGHT);
        m.addInstruction("right to buffer, found ;", '*', "right to buffer", Action.MOVERIGHT);

        // Remember buffer in state, and move to this_state (t) (GENERALISE)
        m.addInstruction("at buffer", '>', "left to symbol >", Action.MOVELEFT);
        m.addInstruction("left to symbol >", '*', "left to symbol >", Action.MOVELEFT);

        // remove this_state marker, then look for symbol >
        m.addInstruction("left to symbol >", 't', "right to symbol >", ' ');
        m.addInstruction("right to symbol >", '*', "right to symbol >", Action.MOVERIGHT);
        m.addInstruction("right to symbol >", 'b', "check for symbol >", Action.MOVERIGHT);
        m.addInstruction("check for symbol >", '>', "found symbol", Action.MOVERIGHT);
        m.addInstruction("check for symbol >", '*', "right to symbol >", Action.MOVERIGHT);

        // Couldn't find symbol > in state a1
        m.addInstruction("check for symbol >", ';', "h", Action.MOVERIGHT);

        // Now that we are in the right state and symbol section, we can set
        // the next state.  First, mark that this is the correct section with a 'i',
        // which will be the iterator symbol we'll use later to compare state numbers
        // (to find the next state).  ie. "b> 1 0" becomes "b>i1 0"
        m.addInstruction("found symbol", ' ', "returning to start to mark next state", 'i');

        // Now go to the far left, then go right looking for the 'j' iterator.  If one is found, then
        // that state has already been checked and is wrong, so find the state after that.
        m.addInstruction("returning to start to mark next state", '*', "returning to start to mark next state", Action.MOVELEFT);
        m.addInstruction("returning to start to mark next state", '>', "searching for previously-checked state", Action.MOVERIGHT);
        m.addInstruction("searching for previously-checked state", '*', "searching for previously-checked state", Action.MOVERIGHT);
        m.addInstruction("searching for previously-checked state", 'j', "mark next state", ' ');

        // If no 'j' iterator is found, then this is the first time doing this loop!  Go to the start
        // and choose the first state to check.
        m.addInstruction("searching for previously-checked state", '>', "mark first state", Action.MOVELEFT);
        m.addInstruction("mark first state", '*', "mark first state", Action.MOVELEFT);
        m.addInstruction("mark first state", '>', "mark next state", Action.MOVERIGHT);

        // Regardless of how we got here, our task is now to mark the next state to be checked to
        // see if it should be the next state.  To do this, mark the first
        // bit of the state number with the iterator symbol 'j' (ie. "a 0 1" becomes "aj0 1")
        m.addInstruction("mark next state", '*', "mark next state", Action.MOVERIGHT);
        m.addInstruction("mark next state", 'a', "writing j iterator", Action.MOVERIGHT);
        m.addInstruction("writing j iterator", ' ', "writing j iterator", 'j');
        m.addInstruction("writing j iterator", 'j', "read iterator j", Action.MOVERIGHT);

        for (char digit : binary_language.get()) {
            m.addInstruction("read iterator j", digit, "iterator j = " + digit, Action.MOVERIGHT);

            // Now return to far left, and search for the 'i' iterator.
            m.addInstruction("iterator j = " + digit, '*', "returning to start to find i (j=" + digit + ")", Action.MOVELEFT);
            m.addInstruction("returning to start to find i (j=" + digit + ")", '>', "finding i (j=" + digit + ")", Action.MOVERIGHT);
            m.addInstruction("finding i (j=" + digit + ")", '*', "finding i (j=" + digit + ")", Action.MOVERIGHT);
            m.addInstruction("finding i (j=" + digit + ")", 'i', "found i (j=" + digit + ")", Action.MOVERIGHT);

            // If the iterators match, continue until there are no more state digits to test.
            m.addInstruction("found i (j=" + digit + ")", digit, "iterators match, keep iterating", Action.MOVELEFT);
        }

        // If the iterators match, continue until there are no more state digits to test.
        m.addInstruction("iterators match, keep iterating", 'i', "iterators match, keep iterating", ' ');
        m.addInstruction("iterators match, keep iterating", ' ', "iterators match, keep iterating", Action.MOVERIGHT);
        m.addInstruction("iterators match, keep iterating", '*', "increment i", Action.MOVERIGHT);
        m.addInstruction("increment i", ' ', "returning to start to iterate j", 'i');

        // Now that i has been incremented (ie. "aj0 1" became "a 0j1") do the same to j:
        m.addInstruction("increment i", '*', "returning to start to iterate j", 'i');
        m.addInstruction("returning to start to iterate j", '*', "returning to start to iterate j", Action.MOVELEFT);
        m.addInstruction("returning to start to iterate j", '>', "find and iterate j", Action.MOVERIGHT);
        m.addInstruction("find and iterate j", '*', "find and iterate j", Action.MOVERIGHT);

        // On finding j, delete it and put it after the next digit:
        // NB. this looks different to iterating i because the formatting is different.
        m.addInstruction("find and iterate j", 'j', "iterate j", ' ');
        m.addInstruction("iterate j", ' ', "iterate j", Action.MOVERIGHT);
        m.addInstruction("iterate j", '0', "mark iterator j", Action.MOVERIGHT);
        m.addInstruction("iterate j", '1', "mark iterator j", Action.MOVERIGHT);
        m.addInstruction("mark iterator j", ' ', "writing j iterator", 'j'); // This is where the check loops!!


        // But if there are no more state digits at i
        for (char task : task_language.get()) {
            m.addInstruction("iterators match, keep iterating", task, "returning to start to assert j finished", Action.MOVELEFT);
        }
        // then find j to make sure it also finished:
        m.addInstruction("returning to start to assert j finished", '*', "returning to start to assert j finished", Action.MOVELEFT);
        m.addInstruction("returning to start to assert j finished", '>', "find j to assert finished", Action.MOVERIGHT);
        m.addInstruction("find j to assert finished", '*', "find j to assert finished", Action.MOVERIGHT);
        m.addInstruction("find j to assert finished", 'j', "assert j finished", Action.MOVERIGHT);
        for (char digit : binary_language.get()) {
            m.addInstruction("assert j finished", digit, "assert j finished", Action.MOVERIGHT);
        }

        // If j finished then this is the next state!! The procedure now is:
        //  - Return to far left
        //  - Move right, removing all fail (f) characters
        //  - When 'j' is reached, remove it then find the preceeding 'a' and mark it 't' (this_state)
        //  - Return to far left
        //  - Move right until 'i', then remove it.
        //  - Process the "task" of the instruction
        m.addInstruction("assert j finished", 'b', "assert j finished", Action.MOVERIGHT);

        // But if they don't match:
        m.addInstruction("found i (j=0)", '1', "iterators don't match, reset", Action.MOVELEFT);
        m.addInstruction("found i (j=1)", '0', "iterators don't match, reset", Action.MOVELEFT);
        m.addInstruction("mark iterator j", '*', "iterators don't match, reset", Action.MOVELEFT); // j finished before i
        // Then:
        //  - Return to start, find i, reset it
        //  - form loop, looking for the next unchecked state
        // TODO find i first, reset it, the find j, delete it, then carry on to the next state.  No need for 'fail'!!






    }

    public void run(Tape tape) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
