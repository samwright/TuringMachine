package turing;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 12:14
 */
interface TapeElement {
    char getValue();
    void setValue(char value);
    TapeElement getNext();
    TapeElement getPrev();
    void setNext(TapeElement e);
}
