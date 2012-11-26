package turing;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 23/11/2012
 * Time: 23:40
 */
public interface Machine {
    static final String HALT = "h";
    static final String START = "s";
    static final String WILDCARD_STATE = "*";
    static final char WILDCARD_SYMBOL = '*';

    /**
     * Adds an instruction to the machine.
     *
     * @param state The state the instruction starts in.
     * @param symbol The symbol at the head in order to run this instruction.
     * @param next_state The state the instruction ends on.
     * @param task The task to be run.
     */
    void addInstruction(String state, char symbol, String next_state, char task);

    /**
     * Returns the action to take for the given state and symbol.
     * @param state The state the action is taken on.
     * @param symbol The symbol the action is taken on.
     * @return The action the machine would take.
     */
    Action getAction(String state, char symbol);

    /**
     * Runs the machine until the halt state is reached.
     */
    void run();

    /**
     * Runs n instructions (unless the machine halts first).
     * @param n The number of instructions to be run.
     */
    void run(int n);

    /**
     * Returns whether the machine has reached the HALT state.
     * @return Whether the machine has reached the HALT state.
     */
    boolean isHalted();

    /**
     * Resets the tape head to the start.
     */
    void resetHead();

    /**
     * Resets the machine state to START.
     */
    void resetState();

    /**
     * Reverts the tape to the original set in 'setTape'
     */
    void resetTape();

    /**
     * Returns the current tape as a string.
     * @return The current tape.
     */
    String getTape();

    /**
     * Sets the tape, resetting the head to the first element.
     * @param tape_str The tape as a string.
     */
    void setTape(String tape_str);

    /**
     * Sets the tape object in the machine.
     * @param tape The new tape object.
     */
    void setTape(Tape tape);

    /**
     * Compiles the machine to a string which can be run in the Universal Turing Macine (turing.examples)
     * @return Compiled machine code.
     */
    String compile();

    /**
     * Returns the current configuration of the machine as a string.
     * @return The current machine status
     */
    String toString();


    void setVerbose(boolean verbose);

}
