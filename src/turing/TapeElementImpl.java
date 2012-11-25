package turing;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 12:20
 */
class TapeElementImpl implements TapeElement {
    private char value;
    private TapeElement next, prev;

    public TapeElementImpl(char value, TapeElement next) {
        setValue(value);
        setNext(next);
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public TapeElement getNext() {
        return next;
    }

    public TapeElement getPrev() {
        return prev;
    }

    public void setNext(TapeElement e) {
        this.next = e;
        if (e != null)
            ((TapeElementImpl)e).prev = this;
    }
}
