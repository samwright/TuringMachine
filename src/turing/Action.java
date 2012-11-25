package turing;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 15:44
 */
interface Action {
    /**
     * Returns the state the action moves the machine to.
     * @return The state the action moves the machine to.
     */
    String getNewState();

    /**
     * Tests if task is to move tape head right.
     * @return Whether task is to move right.
     */
    boolean isMoveRight();

    /**
     * Tests if task is to move tape head left.
     * @return Whether task is to move left.
     */
    boolean isMoveLeft();

    /**
     * Returns the task the action performs.
     * @return The task the action performs.
     */
    char getTask();

    /**
     * Tests if the other action has the same new state and task.
     * as this.
     * @param other The Action to compare this to.
     * @return Whether if the other action has the same new state and task.
     */
    boolean equals(Action other);
}
