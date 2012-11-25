package turing.examples;

import org.junit.Test;

import static org.junit.Assert.*;
import turing.Language;
import turing.Operation;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 25/11/2012
 * Time: 01:17
 */
public class CopyWithoutGapTest extends OperationTest {
    @Override
    Operation createInstance(Language language) {
        return new CopyWithoutGap(language);
    }

    @Test
    public void test() {
        setLanguage(new char[]{'0','1','2'});
        assertEquals(">02120212", run(">0212"));
        assertEquals(">     210210", run(">     210"));
    }
}
