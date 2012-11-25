package turing.examples;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 19:08
 */
public class ShiftLeftABCTest {
    @Test
    public void testCall() throws Exception {
        assertEquals(">abc", ShiftLeftABC.run("> abc"));
    }
}
