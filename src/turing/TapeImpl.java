package turing;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 27/11/2012
 * Time: 23:15
 */
public class TapeImpl extends TapeWithoutCheckImpl {
    public TapeImpl(String tape_string) {
        super(tape_string);

        if (tape_string.charAt(0) != START)
            throw new RuntimeException("Tape must begin with a " + START);
        if (tape_string.isEmpty())
            throw new RuntimeException("Tape can't be empty");
    }
}
