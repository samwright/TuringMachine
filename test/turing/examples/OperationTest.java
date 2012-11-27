package turing.examples;

import turing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 25/11/2012
 * Time: 00:07
 */
public abstract class OperationTest {
    Operation op;

    abstract Operation createInstance(Language language);

    public void setLanguage(char[] symbols) {
        Language language = new LanguageImpl(symbols);
        op = createInstance(language);
    }

    public String run(String str) {
        Tape tape = new TapeWithoutCheckImpl(str);
        op.run(tape);
        return tape.toString().trim();
    }
}
