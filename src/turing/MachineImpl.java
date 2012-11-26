package turing;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 13:22
 */
public class MachineImpl implements Machine {
    private Map<String,Map<Character,Action>> instructions = new HashMap<String, Map<Character, Action>>();
    private Tape tape;
    private String initial_tape_str;
    private String state = START;
    private boolean verbose = false;

    public void addInstruction(String state, char symbol, String next_state, char task) {
        Map<Character,Action> valid_symbols = instructions.get(state);
        if (valid_symbols == null) {
            valid_symbols = new HashMap<Character,Action>();
            instructions.put(state, valid_symbols);
        } else if (valid_symbols.containsKey(symbol)) {
            throw new RuntimeException("Instruction already exists for state \'" + state
                + "\' and symbol \'" + symbol + "\'");
        }
        valid_symbols.put(symbol, new ActionImpl(next_state, task));
    }

    public Action getAction(String state, char symbol) {
        Map<Character,Action> valid_symbols = instructions.get(state);
        if (valid_symbols == null) {
            valid_symbols = instructions.get(WILDCARD_STATE);
            if (valid_symbols == null)
                return null;
        }

        Action action = valid_symbols.get(symbol);
        if (action == null) {
            action = valid_symbols.get(WILDCARD_SYMBOL);
        }

        return action;
    }

    public void run() {
        while(!isHalted()) {
            runOnce();
        }
    }

    public void run(int n) {
        for (int i=0; i<n; ++i) {
            if (isHalted())
                return;
            runOnce();
        }
    }

    public boolean isHalted() {
        return state.equals(HALT);
    }

    public void resetHead() {
        tape.reset();
    }

    public void resetState() {
        state = START;
    }

    public void resetTape() {
        setTape(initial_tape_str);
    }

    public String getTape() {
        return tape.toString();
    }

    public void setTape(String tape_str) {
        initial_tape_str = tape_str;
        tape = new TapeImpl(tape_str);
    }

    public void setTape(Tape tape) {
        this.tape = tape;
        initial_tape_str = tape.toString();
    }

    public String compile() {
        Map<String, Map<Character,Action>> instructions_copy = new HashMap<String, Map<Character, Action>>();
        //Map<Character,Action> valid_instructions.get("h")
        Map<String, String> state_mapping = new HashMap<String, String>();

        state_mapping.put("h", intToSparseBinary(2));
        state_mapping.put("s", intToSparseBinary(1));

        //Map<Character, Character>

        int i=3;
        for (Map.Entry<String, Map<Character,Action>> e : instructions.entrySet()) {
            if (!state_mapping.containsKey(e.getKey()))
                state_mapping.put(e.getKey(), intToSparseBinary(i++));
        }
        return null;
    }

    private String intToSparseBinary(int num) {
        String binary = Integer.toBinaryString(num);
        StringBuilder sbuf = new StringBuilder();
        sbuf.append('a');
        for (int i=0; i<binary.length(); ++i) {
            sbuf.append(' ');
            sbuf.append(binary.charAt(i));
        }
        return sbuf.toString();
    }

    private void runOnce() {
        Action action = getAction(state, tape.read());

        if (action == null)
            throw new RuntimeException("No instruction exists for state \'" + state
                    + "\' and symbol \'" + tape.read()+"\'");

        if (action.isMoveLeft())
            tape.moveLeft();
        else if (action.isMoveRight())
            tape.moveRight();
        else
            tape.write(action.getTask());

        state = action.getNewState();

        if (verbose)
            System.out.println(this);
    }

    @Override
    public String toString() {
        String tape_string = tape.toString();

        StringBuilder spaces = new StringBuilder();
        for (int i=0; i<(3 + tape.getPosition() + state.length()); ++i) {
            spaces.append(' ');
        }

        return String.format("(%s, %s)\n%s^", state, tape_string, spaces.toString());
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
