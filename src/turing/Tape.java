package turing;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 23/11/2012
 * Time: 21:35
 */
public interface Tape {

    /**
     * Overwrite the tape under the head to 'symbol'.
     * @param symbol The value to be written to tape.
     */
    void write(char symbol);

    /**
     * Move the tape head to the left.
     */
    void moveLeft();

    /**
     * Move the tape head to the right.
     */
    void moveRight();

    /**
     * Reads the tape under the head.
     * @return The symbol at the head.
     */
    char read();

    /**
     * Returns the head to the beginning of the tape.
     */
    void reset();

    /**
     * Returns the current position on the tape.
     * @return The index of the head on the tape.
     */
    int getPosition();

    /**
     * Returns the tape as a string
     * @return Tape as a string.
     */
    String toString();
}
