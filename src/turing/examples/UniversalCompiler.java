package turing.examples;

import turing.*;

import java.util.HashMap;
import java.util.Map;

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
public class UniversalCompiler implements UniversalOperation {
    private final Machine m = new MachineImpl();

    public static final char INST_START = '(';
    public static final char INST_END = ')';
    public static final char INST_DELIM = ',';

    public UniversalCompiler(Language language) {
        Language state_name_language = new LanguageImpl(new char[]{'0', '1', 'a'});
        Language binary_language = new LanguageImpl(new char[]{'0', '1'});

        Map<Character,Character> task_mapping = new HashMap<Character, Character>();

        task_mapping.put(Action.MOVELEFT, UniversalExecutor.MOVELEFT);
        task_mapping.put(Action.MOVERIGHT, UniversalExecutor.MOVERIGHT);
        task_mapping.put(UniversalExecutor.HALT, UniversalExecutor.HALT);

        //m.setVerbose(true);

        // We'll put a 'p' at the beginning, and a '=' at the end
        m.addInstruction("s", '>', "going right to mark start", Action.MOVERIGHT);
        m.addInstruction("going right to mark start", '*', "going right to mark start", Action.MOVERIGHT);



        m.addInstruction("going right to mark start", INST_START, "going right to mark end", UniversalExecutor.BUFFER_POINTER);



        m.addInstruction("going right to mark end", '*', "going right to mark end", Action.MOVERIGHT);
        m.addInstruction("going right to mark end", INST_END, "going right to mark end - am I at the end?", Action.MOVERIGHT);
        m.addInstruction("going right to mark end - am I at the end?", INST_START, "going right to mark end", Action.MOVERIGHT);
        m.addInstruction("going right to mark end - am I at the end?", ' ', "mark writing iterator to right", UniversalExecutor.START);
        m.addInstruction("mark writing iterator to right", UniversalExecutor.START, "mark writing iterator", Action.MOVERIGHT);
        m.addInstruction("mark writing iterator", ' ', "go left to mark reading iterator", UniversalExecutor.ITER_J);

        // The 'j' iterator is the marker we'll use to keep track of where to write next.
        // The 'i' iterator is put where we've read a value to move to j.
        m.addInstruction("go left to mark reading iterator", '*', "go left to mark reading iterator", Action.MOVELEFT);
        m.addInstruction("go left to mark reading iterator", UniversalExecutor.BUFFER_POINTER, "mark reading iterator for state", Action.MOVERIGHT);

        // The "check for next state" is the start of the main loop!  If it sees a '(' it starts the reading/writing process.
        m.addInstruction("check for next state", INST_START, "mark reading iterator for state", Action.MOVERIGHT);

        for (char digit : state_name_language.get()) {
            // Read the digit into state, then replace with the 'i' iterator
            m.addInstruction("mark reading iterator for state", digit, "read state digit " + digit, UniversalExecutor.ITER_I);

            // Keep going right until the 'j' iterator.
            m.addInstruction("read state digit " + digit, '*', "read state digit " + digit, Action.MOVERIGHT);

            // We want to write one-to-the-right-of 'j', then put 'j' one-to-the-right-of that, eg. "=j" -> "= 1j"
            m.addInstruction("read state digit " + digit, UniversalExecutor.ITER_J, "go right then write state digit " + digit, ' ');
            m.addInstruction("go right then write state digit " + digit, ' ', "write state digit " + digit, Action.MOVERIGHT);
            m.addInstruction("write state digit " + digit, ' ', "go one right then write j for state digit after " + digit, digit);
            m.addInstruction("go one right then write j for state digit after " + digit, digit, "write j for state digit after " + digit, Action.MOVERIGHT);
            m.addInstruction("write j for state digit after " + digit, ' ', "move left for state digit after " + digit, UniversalExecutor.ITER_J);

            // Now go back to the 'i' iterator and replace with the taken digit
            m.addInstruction("move left for state digit after " + digit, '*', "move left for state digit after " + digit, Action.MOVELEFT);
            m.addInstruction("move left for state digit after " + digit, UniversalExecutor.ITER_I, "replaced state digit", digit);

            // Now loop back to "mark reading iterator for state"!
            m.addInstruction("replaced state digit", digit, "mark reading iterator for state", Action.MOVERIGHT);
        }

        // If there are no more state digits to read, there will be a ','.
        m.addInstruction("mark reading iterator for state", INST_DELIM, "read symbol", Action.MOVERIGHT);

        for (char symbol : language.get()) {
            // Read the symbol
            m.addInstruction("read symbol", symbol, "need to write i, read symbol " + symbol, Action.MOVERIGHT);

            // The symbol is always 1 character long, so the next character is ','.  We'll put the 'i' there.
            m.addInstruction("need to write i, read symbol " + symbol, INST_DELIM, "read symbol " + symbol, UniversalExecutor.ITER_I);

            // Keep going right until 'j'
            m.addInstruction("read symbol " + symbol, '*', "read symbol " + symbol, Action.MOVERIGHT);

            // The 'j' is replaced with a 'b', then followed by the symbol, then by 'j', eg. " a 1 0j" -> " a 1 0bzj" (where symbol = 'z')
            m.addInstruction("read symbol " + symbol, UniversalExecutor.ITER_J, "write symbol " + symbol, UniversalExecutor.SYMBOL_START);
            m.addInstruction("write symbol " + symbol, UniversalExecutor.SYMBOL_START, "write symbol " + symbol, Action.MOVERIGHT);
            m.addInstruction("write symbol " + symbol, ' ', "move right to write j for next_state digit", symbol);
            m.addInstruction("move right to write j for next_state digit", symbol, "write j for next_state digit", Action.MOVERIGHT);

        }
        m.addInstruction("write j for next_state digit", ' ', "move left for next_state", UniversalExecutor.ITER_J);

        // Now we go left to start reading the next_state digit after 'i'.
        m.addInstruction("move left for next_state", '*', "move left for next_state", Action.MOVELEFT);
        m.addInstruction("move left for next_state", 'i', "replace delim before reading next_state digit", INST_DELIM);

        // We don't want the copy the 'a' from the next_state identifier, so skip it:
        m.addInstruction("replace delim before reading next_state digit", ',', "replace delim before reading next_state digit", Action.MOVERIGHT);
        m.addInstruction("replace delim before reading next_state digit", 'a', "read next_state digit", Action.MOVERIGHT);


        for (char digit : binary_language.get()) {
            // If the next digit is indeed a digit, read it and replace with the 'i'.
            m.addInstruction("read next_state digit", digit, "read next_state digit " + digit, UniversalExecutor.ITER_I);

            // Now go right to j
            m.addInstruction("read next_state digit " + digit, '*', "read next_state digit " + digit, Action.MOVERIGHT);
            m.addInstruction("read next_state digit " + digit, UniversalExecutor.ITER_J, "go right to write next_state digit " + digit, ' ');

            // We will put the digit one to the right of where 'j' was
            m.addInstruction("go right to write next_state digit " + digit, ' ', "write next_state digit " + digit, Action.MOVERIGHT);
            m.addInstruction("write next_state digit " + digit, ' ', "go right to write j then replace next_state digit " + digit, digit);

            // Then put 'j' one to the right of the digit
            m.addInstruction("go right to write j then replace next_state digit " + digit, digit, "write j then replace next_state digit " + digit, Action.MOVERIGHT);
            m.addInstruction("write j then replace next_state digit " + digit, ' ', "go left to replace next_state digit " + digit, UniversalExecutor.ITER_J);

            // Now go left to find and replace 'i'
            m.addInstruction("go left to replace next_state digit " + digit, '*', "go left to replace next_state digit " + digit, Action.MOVELEFT);
            m.addInstruction("go left to replace next_state digit " + digit, 'i', "move one right to get next next_state digit", digit);

            // Go one right, and loop!
            m.addInstruction("move one right to get next next_state digit", digit, "read next_state digit", Action.MOVERIGHT);
        }

        // When there are no more next_state digits to read, move on to the task digit
        // Replace the ',' with 'i' then move right to read task digit
        m.addInstruction("read next_state digit", INST_DELIM, "read task digit", UniversalExecutor.ITER_I);
        m.addInstruction("read task digit", UniversalExecutor.ITER_I, "read task digit", Action.MOVERIGHT);

        // If the task is to write a symbol
        for (char symbol : language.get()) {
            // Read the symbol
            m.addInstruction("read task digit", symbol, "task is to write symbol " + symbol, Action.MOVERIGHT);

            // Then move right to 'j'
            m.addInstruction("task is to write symbol " + symbol, '*', "task is to write symbol " + symbol, Action.MOVERIGHT);

            // Then replace 'j' with the write symbol 'w'
            m.addInstruction("task is to write symbol " + symbol, UniversalExecutor.ITER_J, "move one to right then write task symbol " + symbol, UniversalExecutor.WRITE);
            m.addInstruction("move one to right then write task symbol " + symbol, UniversalExecutor.WRITE, "write task symbol " + symbol, Action.MOVERIGHT);

            // Now write the symbol to be written by performing the task
            m.addInstruction("write task symbol " + symbol, ' ', "write ; to right then replace task symbol " + symbol, symbol);

            // Then add a ';' after
            m.addInstruction("write ; to right then replace task symbol " + symbol, symbol, "write ; then replace task symbol " + symbol, Action.MOVERIGHT);

            m.addInstruction("write ; then replace task symbol " + symbol, ' ', "write j to right then replace task symbol " + symbol, UniversalExecutor.STATE_END);

            // Now put 'j' after the write symbol
            m.addInstruction("write j to right then replace task symbol " + symbol, UniversalExecutor.STATE_END, "write j then replace task symbol " + symbol, Action.MOVERIGHT);
            m.addInstruction("write j then replace task symbol " + symbol, ' ', "go left to replace write task symbol " + symbol, UniversalExecutor.ITER_J);


            // Now return left to replace 'i' with ',' and look for more symbols or tasks! (near-copy of this is in next for-loop too.)
            m.addInstruction("go left to replace write task symbol " + symbol, '*', "go left to replace write task symbol " + symbol, Action.MOVELEFT);
            m.addInstruction("go left to replace write task symbol " + symbol, UniversalExecutor.ITER_I, "skip , and symbol " + symbol, INST_DELIM);
            m.addInstruction("skip , and symbol " + symbol, INST_DELIM, "skip , and symbol " + symbol, Action.MOVERIGHT);
            m.addInstruction("skip , and symbol " + symbol, symbol, "look for more tasks", Action.MOVERIGHT);

        }

        // if the task is to go right or left or halt
        for (Map.Entry<Character,Character> e : task_mapping.entrySet()) {
            char task_in = e.getKey();
            char task_out = e.getValue();
            // Read the task
            m.addInstruction("read task digit", task_in, "task is " + task_in, Action.MOVERIGHT);

            // Then move right to 'j'
            m.addInstruction("task is " + task_in, '*', "task is " + task_in, Action.MOVERIGHT);

            // Then replace 'j' with the task
            m.addInstruction("task is " + task_in, UniversalExecutor.ITER_J, "move one to right to write ;, task was " + task_in, task_out);

            // Put a ';' after the task
            m.addInstruction("move one to right to write ;, task was " + task_in, task_out, "write ;, task was " + task_in, Action.MOVERIGHT);
            m.addInstruction("write ;, task was " + task_in, ' ', "move one to right to write j, task was " + task_in, UniversalExecutor.STATE_END);

            m.addInstruction("move one to right to write j, task was " + task_in, UniversalExecutor.STATE_END, "write j, task was " + task_in, Action.MOVERIGHT);

            // Put 'j' one to the right of the task
            m.addInstruction("write j, task was " + task_in, ' ', "go left to replace task symbol " + task_in, UniversalExecutor.ITER_J);

            // Now return left to replace i and look for more symbols or tasks!
            m.addInstruction("go left to replace task symbol " + task_in, '*', "go left to replace task symbol " + task_in, Action.MOVELEFT);
            m.addInstruction("go left to replace task symbol " + task_in, UniversalExecutor.ITER_I, "skip , and symbol " + task_in, INST_DELIM);
            m.addInstruction("skip , and symbol " + task_in, INST_DELIM, "skip , and symbol " + task_in, Action.MOVERIGHT);
            m.addInstruction("skip , and symbol " + task_in, task_in, "look for more tasks", Action.MOVERIGHT);

        }


        // If there's another symbol in this state, loop!
        m.addInstruction("look for more tasks", INST_DELIM, "read symbol", Action.MOVERIGHT);

        // If there's no other symbol, go to next state
        m.addInstruction("look for more tasks", INST_END, "check for next state", Action.MOVERIGHT);


        // When there are no more states, replace j with ';':
        m.addInstruction("check for next state", UniversalExecutor.START, "end of states", Action.MOVERIGHT);
        m.addInstruction("end of states", '*', "end of states", Action.MOVERIGHT);

        // Go right to replace "j" with ";>pj"
        m.addInstruction("end of states", UniversalExecutor.ITER_J, "write > one to right for copying input", UniversalExecutor.STATE_END);
        m.addInstruction("write > one to right for copying input", UniversalExecutor.STATE_END, "write > one to right for copying input", Action.MOVERIGHT);
        m.addInstruction("write > one to right for copying input", ' ', "write p one to right for copying input", Tape.START);
        m.addInstruction("write p one to right for copying input", Tape.START, "write p one to right for copying input", Action.MOVERIGHT);
        m.addInstruction("write p one to right for copying input", ' ', "write j one to right for copying input", UniversalExecutor.BUFFER_POINTER);
        m.addInstruction("write j one to right for copying input", UniversalExecutor.BUFFER_POINTER, "write j one to right for copying input", Action.MOVERIGHT);
        m.addInstruction("write j one to right for copying input", ' ', "go past new input to initiate copy", UniversalExecutor.ITER_J);

        // Go to very beginning (of input)

        m.addInstruction("go past new input to initiate copy", '*', "go past new input to initiate copy", Action.MOVELEFT);
        m.addInstruction("go past new input to initiate copy", UniversalExecutor.STATE_END, "go past instructions to initiate copy", Action.MOVELEFT);

        m.addInstruction("go past instructions to initiate copy", '*', "go past instructions to initiate copy", Action.MOVELEFT);
        m.addInstruction("go past instructions to initiate copy", UniversalExecutor.BUFFER_POINTER, "go left to initiate copying", Action.MOVELEFT);

        m.addInstruction("go left to initiate copying", '*', "go left to initiate copying", Action.MOVELEFT);
        m.addInstruction("go left to initiate copying", Tape.START, "initiate copying", Action.MOVERIGHT);

        // This is the start of the loop: being in "initiating copying" state while sat on the new symbol to copy,
        // and j is where the symbol needs to go.

        for (char symbol : language.get()) {
            m.addInstruction("initiate copying", symbol, "copy input " + symbol, UniversalExecutor.ITER_I);
            m.addInstruction("copy input " + symbol, '*', "copy input " + symbol, Action.MOVERIGHT);

            // Keep going right until 'j', then "j" becomes "zj" (where 'z' is symbol)
            m.addInstruction("copy input " + symbol, UniversalExecutor.ITER_J, "write j next, wrote input " + symbol, symbol);
            m.addInstruction("write j next, wrote input " + symbol, symbol, "write j, wrote input " + symbol, Action.MOVERIGHT);
            m.addInstruction("write j, wrote input " + symbol, ' ', "go left to copy after writing " + symbol, UniversalExecutor.ITER_J);

            // Go left until i, then replace it with symbol
            m.addInstruction("go left to copy after writing " + symbol, '*', "go left to copy after writing " + symbol, Action.MOVELEFT);
            m.addInstruction("go left to copy after writing " + symbol, UniversalExecutor.ITER_I, "move right to copy next", symbol);
            m.addInstruction("move right to copy next", symbol, "initiate copying", Action.MOVERIGHT);
        }


        // If there's no more input to copy, replace 'p' with '(' and go right
        m.addInstruction("initiate copying", UniversalExecutor.BUFFER_POINTER, "go right past = to mark start state" , INST_START);

        // Go past the state defs to the compiled code to look for starting state:
        m.addInstruction("go right past = to mark start state", '*', "go right past = to mark start state", Action.MOVERIGHT);
        m.addInstruction("go right past = to mark start state", '=', "go right to mark start state", Action.MOVERIGHT);

        // Look for " a 1b" which marks the starting state
        m.addInstruction("go right to mark start state", '*', "go right to mark start state", Action.MOVERIGHT);
        m.addInstruction("go right to mark start state", UniversalExecutor.STATE_START, "go right to check start state digit 1", Action.MOVERIGHT);

        // Skipping spaces, we want a 1 then a b:
        m.addInstruction("go right to check start state digit 1", ' ', "go right to check start state digit 1", Action.MOVERIGHT);
        m.addInstruction("go right to check start state digit 1", '1', "go right to check start state digit b", Action.MOVERIGHT);
        m.addInstruction("go right to check start state digit b", UniversalExecutor.SYMBOL_START, "found start state, go left to mark it", Action.MOVELEFT);

        // Move left to the space before 'a' and mark 't'
        m.addInstruction("found start state, go left to mark it", '*', "found start state, go left to mark it", Action.MOVELEFT);
        m.addInstruction("found start state, go left to mark it", 'a', "found start state, mark it", Action.MOVELEFT);
        m.addInstruction("found start state, mark it", ' ', "go right to delete j", UniversalExecutor.THIS_STATE);

        // But if it wasn't the start state, go right to the next one:
        m.addInstruction("go right to check start state digit 1", '*', "go right to mark start state", Action.MOVERIGHT);
        m.addInstruction("go right to check start state digit b", '*', "go right to mark start state", Action.MOVERIGHT);

        // Go to right to delete j
        m.addInstruction("go right to delete j", '*', "go right to delete j", Action.MOVERIGHT);
        m.addInstruction("go right to delete j", UniversalExecutor.ITER_J, "go left to execute", ' ');

        // Go left to starting point (=) of executor
        m.addInstruction("go left to execute", '*', "go left to execute", Action.MOVELEFT);
        m.addInstruction("go left to execute", UniversalExecutor.START, "h", UniversalExecutor.START); // This is where the executor starts


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
