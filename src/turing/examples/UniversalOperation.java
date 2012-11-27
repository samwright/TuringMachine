package turing.examples;

import turing.Machine;
import turing.Operation;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 27/11/2012
 * Time: 15:17
 */
public interface UniversalOperation extends Operation {

    /**
     * Returns the machine in the universal operation
     * @return The machine.
     */
    Machine getMachine();
}
