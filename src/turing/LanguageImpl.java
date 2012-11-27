package turing;

import turing.examples.UniversalCompiler;
import turing.examples.UniversalExecutor;

import java.util.Collections;
import java.util.HashSet;
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
    private static final Set<Character> universal_reserved = new HashSet<Character>();

    static {
        reserved.add(MachineImpl.WILDCARD_SYMBOL);

        //reserved.add(TapeImpl.EMPTY);
        //reserved.add(TapeImpl.START);


        reserved.add(UniversalCompiler.INST_DELIM);
        reserved.add(UniversalCompiler.INST_START);
        reserved.add(UniversalCompiler.INST_END);


        universal_reserved.add(UniversalExecutor.STATE_START);
        universal_reserved.add(UniversalExecutor.SYMBOL_START);
        universal_reserved.add(UniversalExecutor.START);
        universal_reserved.add(UniversalExecutor.STATE_END);
        universal_reserved.add(UniversalExecutor.ITER_I);
        universal_reserved.add(UniversalExecutor.ITER_J);
        universal_reserved.add(UniversalExecutor.BUFFER_POINTER);

        universal_reserved.add(Machine.HALT.charAt(0));
    }

    public LanguageImpl(char[] array) {
        for (char ch : array) {
            chars.add(ch);
        }
        checkValid();
    }

    public void checkValid() {
        if (chars.removeAll(reserved))
            throw new RuntimeException("Language contained a reserved character from LanguageImpl");
    }

    public Set<Character> get() {
        return Collections.unmodifiableSet(chars);
    }

    @Override
    public void checkUniversal() {
        if (new HashSet<Character>(chars).removeAll(reserved))
            throw new RuntimeException("Language contained a reserved character for a universal language");
    }
}
