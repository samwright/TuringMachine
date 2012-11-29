package turing;

import java.util.*;

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

    @Override
    public void clearInstructions() {
        instructions = new HashMap<String, Map<Character, Action>>();
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

    public String getInstructions(Language language) {
        if (null != instructions.get(WILDCARD_STATE))
            throw new UnsupportedOperationException("Wildcard state compilation not supported yet");

        language.checkUniversal();

        char symbol;
        Action action;
        StringBuilder sbuf = new StringBuilder();
        Action wildcard_action;
        Map<String,String> binary_states = new HashMap<String,String>();
        int state_number = 2;

        binary_states.put(Machine.HALT, "a" + Integer.toBinaryString(0));
        binary_states.put(Machine.START, "a" + Integer.toBinaryString(1));

        for (String state : instructions.keySet()) {
            if (!state.equals(Machine.START) && !state.equals(Machine.HALT))
                binary_states.put(state, "a" + Integer.toBinaryString(state_number++));
        }

        for (String state : instructions.keySet()) {
            wildcard_action = null;
            sbuf.append(String.format("(%s", binary_states.get(state)));

            if (state.equals("h"))
                throw new RuntimeException("Can't have instruction starting on 'h'");

            for (Map.Entry<Character, Action> e : instructions.get(state).entrySet()){
                symbol = e.getKey();
                action = e.getValue();

                if (symbol == '*') {
                    wildcard_action = action;
                    continue;
                }

                sbuf.append(String.format(",%s,%s,%s", symbol,
                        binary_states.get(action.getNewState()), action.getTask()));
            }

            if (wildcard_action != null) {
                for (char lang_symbol : language.get()) {
                    if (!instructions.get(state).containsKey(lang_symbol))
                        sbuf.append(String.format(",%s,%s,%s", lang_symbol,
                                binary_states.get(wildcard_action.getNewState()), wildcard_action.getTask()));
                }
            }

            sbuf.append(')');
        }

        // When the false halt state (a0) is reached, perform the halt task (which
        // lets the universal executor clean the buffer before moving to the REAL halt state).
        sbuf.append("(a0");
        for (char lang_symbol : language.get()) {
            sbuf.append(String.format(",%s,a0,h", lang_symbol));
        }
        sbuf.append(')');

        return sbuf.toString();
    }

    public Map<String, Map<Character, Action>> getLowLevelInstructions() {
        return Collections.unmodifiableMap(instructions);
    }


    public void appendMachine(Machine other) {
        Map<String,Map<Character,Action>> other_instructions = Collections.unmodifiableMap(((MachineImpl) other).instructions);
        String identifier = String.valueOf(other.hashCode());

        if (!other_instructions.containsKey(START))
            throw new RuntimeException("Appended machine didn't contain a START state");
        if (other_instructions.containsKey(HALT))
            throw new RuntimeException("Appended machine contained instructions starting from HALT");

        Map<String,String> state_translation = new HashMap<String, String>();
        state_translation.put(HALT, HALT);
        List<Action> actions_to_update = new LinkedList<Action>();

        if (instructions.isEmpty()) {
            for (String other_state : other_instructions.keySet()) {
                state_translation.put(other_state, other_state);
            }
        } else {
            for (String other_state : other_instructions.keySet()) {
                state_translation.put(other_state, String.format("%s : %s", identifier, other_state));
            }
            // update this halt to other start
            for (Map<Character, Action> map : this.instructions.values()) {
                for (Action action : map.values()) {
                    if (action.getNewState().equals(HALT)) {
                        // Defer update to after adding other states
                        // (so appending this to this works)
                        actions_to_update.add(action);
                    }
                }
            }
        }

        // Add other.instructions to added_instructions, prepending identifiers in all states
        // except for HALT (ie. as per state_translation mapping)
        Action other_action, copied_action;
        String state;

        Map<String,Map<Character,Action>> added_instructions = new HashMap<String, Map<Character, Action>>();
        Map<Character, Action> new_map;

        for (Map.Entry<String, Map<Character,Action>> e : other_instructions.entrySet()) {
            state = e.getKey();

            new_map = added_instructions.get(state_translation.get(state));
            if (new_map == null) {
                new_map = new HashMap<Character, Action>();
                added_instructions.put(state_translation.get(state), new_map);
            }

            for (Map.Entry<Character, Action> symbol_action : e.getValue().entrySet()) {
                other_action = symbol_action.getValue();
                copied_action = new ActionImpl(state_translation.get(other_action.getNewState()), other_action.getTask());
                new_map.put(symbol_action.getKey(), copied_action);
            }
        }

        // Add the new states to this
        instructions.putAll(added_instructions);

        // Update old this instructions pointing to HALT
        // to now point to the start state from new instructions
        String other_start = state_translation.get(START);
        for (Action action : actions_to_update) {
            action.setNewState(other_start);
        }
    }

    private void runOnce() {
        Action action = getAction(state, tape.read());

        if (action == null) {
//            System.out.println("instructions: " + Arrays.toString(instructions.get(state).keySet().toArray()));
            throw new RuntimeException("No instruction exists for state \'" + state
                    + "\' and symbol \'" + tape.read()+"\'\n" + toString());
        }

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
