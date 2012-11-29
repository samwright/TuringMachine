package turing.app;

import turing.Action;
import turing.MachineImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 29/11/2012
 * Time: 00:45
 */
public class GuiMachine extends MachineImpl {
    public void setInstructions(List<String[]> instructions_lst) {
        clearInstructions();
        for (String[] instruction : instructions_lst) {
            char task;

            try {
                if (instruction[3].equals(">>"))
                    task = Action.MOVERIGHT;
                else if (instruction[3].equals("<<"))
                    task = Action.MOVELEFT;
                else
                    task = instruction[3].charAt(0);
            } catch (NullPointerException e) {
                throw new RuntimeException("Task for state: '" + instruction[0] + "' and symbol + '" + instruction[1] +
                    "' is empty!");
            }

            addInstruction(instruction[0], instruction[1].charAt(0),
                    instruction[2], task);
        }
    }

    public List<String[]> getInstructionsList() {
        Map<String,Map<Character,Action>> instructions = getLowLevelInstructions();
        String state, symbol, new_state, task;
        List<String[]> instructions_lst = new ArrayList<String[]>();

        for (Map.Entry<String,Map<Character,Action>> entry : instructions.entrySet()) {
            state = entry.getKey();

            for (Map.Entry<Character,Action> symbol_entry : entry.getValue().entrySet()) {
                symbol = String.valueOf(symbol_entry.getKey());
                new_state = symbol_entry.getValue().getNewState();
                task = String.valueOf(symbol_entry.getValue().getTask());

                instructions_lst.add(new String[]{state, symbol, new_state, task});
            }

        }
        return instructions_lst;
    }
}
