package turing.examples;

import org.junit.Test;
import static org.junit.Assert.*;
import turing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 22:57
 */
public class ShiftLeftTest extends OperationTest {

    @Test
    public void testRun() throws Exception {
        setLanguage(new char[]{'0','1','2'});
        assertEquals(">021", run("> 021"));

        setLanguage(new char[]{'a', '1', '2'});
        assertEquals(">021", run("> 021"));
    }

    @Override
    Operation createInstance(Language language) {
        return new ShiftLeft(language);
    }
}
