package turing.examples;

import org.junit.Test;
import turing.Language;
import turing.Operation;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 25/11/2012
 * Time: 17:20
 */
public class UniversalExecutorTest extends OperationTest {
    @Test
    public void testRun() throws Exception {
        setLanguage(new char[]{'1', '2', '3'});
        assertEquals("123", run("=ta 1b> 1 0w1; a 1 0b1 1 0»b  1 1w2; a 1 1b2 1 1»b  0w3; a 0b3 0h;;>p"));
    }


    Operation createInstance(Language language) {
        return new UniversalExecutor(language);
    }

    @Override
    public String run(String str) {
        String result = super.run(str);
        String[] lst = result.split(";; *");
        return lst[lst.length-1];
    }
}

