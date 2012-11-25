package turing.examples;

import org.junit.Test;

import static org.junit.Assert.*;
import turing.Language;
import turing.Operation;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 25/11/2012
 * Time: 00:07
 */
public class CopyAfterGapTest extends OperationTest {
    @Override
    Operation createInstance(Language language) {
        return new CopyAfterGap(language);
    }

    @Test
    public void testCopy() {
        setLanguage(new char[]{'1','2','0'});
        assertEquals(">021 021", run(">021"));
        assertEquals(">   021 021", run(">   021"));
    }
}
