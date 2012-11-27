package turing;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 13:31
 */
public class ActionImpl implements Action {
    private String new_state;
    private char task;

    public ActionImpl(String new_state, char task) {
        setNewState(new_state);
        setTask(task);
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
    public void setTask(char task) {
        this.task = task;
    }

    @Override
    public void setNewState(String new_state) {
        this.new_state = new_state;
    }

    @Override
    public boolean equals(Action other) {
        return other != null && new_state.equals(other.getNewState()) && task == other.getTask();
    }
}
