package turing.examples;

import turing.Language;
import turing.Operation;
import turing.Tape;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 25/11/2012
 * Time: 01:00
 */
public class CopyWithoutGap implements Operation {
    private Operation copy_after_gap, shift_left;

    public CopyWithoutGap(Language language) {
        copy_after_gap = new CopyAfterGap(language);
        shift_left = new ShiftLeft(language);
    }

    public void run(Tape tape) {
        copy_after_gap.run(tape);
        shift_left.run(tape);
    }
}
