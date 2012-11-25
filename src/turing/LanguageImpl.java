package turing;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 19:50
 */
public class LanguageImpl implements Language {
    private final Set<Character> chars = new HashSet<Character>();
    private static final Set<Character> reserved = new HashSet<Character>();

    static {
        reserved.add(MachineImpl.WILDCARD_SYMBOL);
        reserved.add(TapeImpl.EMPTY);
        reserved.add(TapeImpl.START);
    }

    public LanguageImpl(char[] array) {
        for (char ch : array) {
            chars.add(ch);
        }
        checkValid();
    }

    public LanguageImpl(List<Character> lst) {
        for (char ch : lst) {
            chars.add(ch);
        }
        checkValid();
    }

    private void checkValid() {
        if (chars.removeAll(reserved))
            throw new RuntimeException("Language contained a reserved character");
    }

    public Set<Character> get() {
        return Collections.unmodifiableSet(chars);
        //return new HashSet<Character>(chars);
    }
}
