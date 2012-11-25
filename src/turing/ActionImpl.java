package turing;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 13:31
 */
class ActionImpl implements Action {
    public final char MOVELEFT = '<';
    public final char MOVERIGHT = '>';
    private final String new_state;
    private final char task;

    public ActionImpl(String new_state, char task) {
        this.new_state = new_state;
        this.task = task;
    }

    @Override
    public String getNewState() {
        return new_state;
    }

    @Override
    public boolean isMoveRight() {
        return task == MOVERIGHT;
    }

    @Override
    public boolean isMoveLeft() {
        return task == MOVELEFT;
    }

    @Override
    public char getTask() {
        return task;
    }

    @Override
    public boolean equals(Action other) {
        return other != null && new_state.equals(other.getNewState()) && task == other.getTask();
    }
}
