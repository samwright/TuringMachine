package turing;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 23:12
 */
public class LanguageImplTest {
    Language language;

    @Test
    public void testInitialise() throws Exception {
        language = new LanguageImpl(new char[]{'0','1'});
        language = new LanguageImpl(new char[]{'a', 'b'});

        try {
            language = new LanguageImpl(new char[]{TapeImpl.EMPTY});
        } catch (RuntimeException err) {
        }

        try {
            language = new LanguageImpl(new char[]{MachineImpl.WILDCARD_SYMBOL});
        } catch (RuntimeException err) {
        }

        try {
            language = new LanguageImpl(new char[]{TapeImpl.START});
        } catch (RuntimeException err) {
        }
    }

    @Test
    public void testGet() throws Exception {
        language = new LanguageImpl(new char[]{'a', 'b'});
        assertEquals(2, language.get().size());

        assertTrue(language.get().contains('a'));
        assertTrue(language.get().contains('b'));
    }

    @Test
    public void testLanguageImmutable() throws Exception {
        language = new LanguageImpl(new char[]{'a', 'b'});
        Set<Character> charset = language.get();

        try {
            charset.clear();
        } catch (Exception err) {
        }

        assertEquals(2, language.get().size());

        assertTrue(language.get().contains('a'));
        assertTrue(language.get().contains('b'));
    }
}
