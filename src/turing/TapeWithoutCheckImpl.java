package turing;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 23/11/2012
 * Time: 23:54
 */
public class TapeWithoutCheckImpl implements Tape {
    private TapeElement start_element, head;
    private int position;

    public TapeWithoutCheckImpl(String tape_string) {
        for (int i = tape_string.length() - 1; i >= 0; --i) {
            start_element = new TapeElementImpl(tape_string.charAt(i), start_element);
        }

        reset();
    }

    public void write(char symbol) {
        head.setValue(symbol);
    }

    public void moveLeft() {
        TapeElement prev = head.getPrev();
        if (prev != null)
            head = prev;
        else
            throw new RuntimeException("Moved left of start of tape.");

        --position;
    }

    public void moveRight() {
        TapeElement next = head.getNext();
        if (next == null) {
            next = new TapeElementImpl(EMPTY, null);
            head.setNext(next);
        }
        head = next;
        ++position;
    }

    public char read() {
        return head.getValue();
    }

    public void reset() {
        head = start_element;
        position = 0;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        StringBuilder sbuf = new StringBuilder();
        TapeElement itr = start_element;

        do {
            sbuf.append(itr.getValue());
            itr = itr.getNext();
        } while(itr != null);

        return sbuf.toString();
    }

}
