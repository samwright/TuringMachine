package turing;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 21:43
 */
public interface Operation {
    /**
     * Run the operation on the tape.
     * @param tape The tape to run the operation on.
     */
    void run(Tape tape);
}
