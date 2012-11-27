package turing.examples;

import org.junit.Test;
import turing.Language;
import turing.Operation;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 27/11/2012
 * Time: 03:46
 */
public class UniversalCompilerTest extends OperationTest {
    @Test
    public void testRun() throws Exception {
        setLanguage(new char[]{'1', '2', '3', '>', ' '});
        String states = ">123(a10,1,a110,»)(a1,>,a101,»)(a11,2,a100,»)(a100, ,a0,3)(a101, ,a10,1)(a110, ,a11,2)(a0,3,a0,h,2,a0,h, ,a0,h,1,a0,h,>,a0,h)";
        String expected = ">123(a10,1,a110,»)(a1,>,a101,»)(a11,2,a100,»)(a100, ,a0,3)(a101, ,a10,1)(a110, ,a11,2)(a0,3,a0,h,2,a0,h, ,a0,h,1,a0,h,>,a0,h)" +
                "= a 1 0b1 1 1 0r;ta 1b> 1 0 1r; a 1 1b2 1 0 0r; a 1 0 0b  0w3; a 1 0 1b  1 0w1; a 1 1 0b  1 1w2; a 0b3 0h;b2 0h;b  0h;b1 0h;b> 0h;;>p123";
        assertEquals(expected, run(states));
    }


    Operation createInstance(Language language) {
        return new UniversalCompiler(language);
    }

    @Override
    public String run(String str) {
        /*String result = super.run(str);
        String[] lst = result.split(";; *");
        return lst[lst.length - 1];*/
        return super.run(str);
    }
}