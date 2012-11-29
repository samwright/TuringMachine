package turing.app;

import turing.Language;
import turing.LanguageImpl;
import turing.Machine;
import turing.Tape;
import turing.examples.CopyAfterGap;
import turing.examples.CopyWithoutGap;
import turing.examples.ShiftLeft;
import turing.examples.UniversalMachine;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 28/11/2012
 * Time: 11:46
 */
public class TuringApp {
    private JPanel panel1;
    private JTable instructions;
    private JButton run;
    private JSlider speedSlider;
    private JCheckBox atSpeedCheckBox;
    private JButton addStateButton;
    private JScrollPane instructionsScrollPane;
    private JButton saveButton;
    private JButton openButton;
    private JButton saveAsButton;
    private JButton newButton;
    private JButton runOnce;
    private JTextField textField1;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JButton resetButton;
    private JButton copyStatesToClipboardButton;
    private JButton importExampleButton;
    private JComboBox comboBox1;

    private double speed = 1.0;
    private int steps_per_frame = 1;

    private List<String[]> instructions_list = new ArrayList<String[]>();
    private GuiMachine m = new GuiMachine();
    private GuiTape tape;
    private boolean instructions_changed = true;


    private ActionListener machine_operator = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (!m.isHalted())
                    runMachine(steps_per_frame);
                else {
                    run.setText("Run");
                    timer.stop();
                }
            } catch (RuntimeException er) {
                JOptionPane.showMessageDialog(panel1, "Runtime exception: " + er.getMessage());
                instructions_changed = true;
            }
        }
    };
    private Timer timer = new Timer(0, machine_operator);




    public TuringApp() {

        //instructions_list.add(new String[]{Machine.START, String.valueOf(Tape.START), "", ""});
        // import copy example
        m.appendMachine(new CopyAfterGap(new LanguageImpl(new char[]{'0','1','2',' ','>'})).getMachine());
        instructions_list = m.getInstructionsList();
        //

        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int slider_val = source.getValue();
                speed = Math.pow(10, ((double) slider_val) / 10);

                atSpeedCheckBox.setText(String.format("At speed: %.1f/s", speed));
                updateSpeed();
            }
        });
        addStateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int new_row_index = instructions_list.size();
                instructions_list.add(new String[]{"","*","",""});
                instructions.updateUI();
                instructions.setRowSelectionInterval(new_row_index, new_row_index);
                instructions.requestFocus();
            }
        });
        runOnce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instructions_changed)
                    setupMachine();
                try {
                    if (!m.isHalted())
                        runMachine(1);
                } catch (RuntimeException er) {
                    JOptionPane.showMessageDialog(panel1, "Runtime exception: " + er.getMessage());
                    instructions_changed = true;
                }
            }
        });
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instructions_changed)
                    setupMachine();
                if (atSpeedCheckBox.isSelected()) {
                    if (run.getText().equals("Run")) {
                        timer.start();
                        run.setText("Stop");
                    } else {
                        timer.stop();
                        run.setText("Run");
                    }
                } else {
                    try {
                        do {
                            runMachine();
                        } while(!m.isHalted());
                    } catch (RuntimeException er) {
                        JOptionPane.showMessageDialog(panel1, "Runtime exception: " + er.getMessage());
                        instructions_changed = true;
                    }
                }

            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetMachineInput();
            }
        });
        importExampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Import example
                Language language = getLanguage();
                if (language == null)
                    return;

                int example_index = comboBox1.getSelectedIndex();
                Machine example;
                if (example_index == 0)
                    example = new CopyAfterGap(language).getMachine();
                else if (example_index == 1)
                    example = new ShiftLeft(language).getMachine();
                else if (example_index == 2)
                    example = new CopyWithoutGap(language).getMachine();
                else
                    example = new UniversalMachine(language).getMachine();

                m.clearInstructions();
                m.appendMachine(example);
                instructions_list = m.getInstructionsList();
                instructions.updateUI();
            }
        });
        copyStatesToClipboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Language language = getLanguage();
                if (language == null)
                    return;

                StringSelection selection = new StringSelection(m.getInstructions(language));
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TuringApp");
        frame.setContentPane(new TuringApp().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        //TableModel dataModel = new DefaultTableModel(new String[]{"Input State", "Input Symbol", "Output State", "Task"}, 3);
        TableModel instructionsModel = new AbstractTableModel() {
            private String[] headers = new String[]{"Input State", "Input Symbol", "Output State", "Task"};

            @Override
            public String getColumnName(int n) {
                return headers[n];
            }

            @Override
            public int getRowCount() {
                return instructions_list.size();
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public Object getValueAt(int row, int col) {
                return instructions_list.get(row)[col];
            }

            @Override
            public void setValueAt(Object value, int row, int col) {
                String val = String.valueOf(value);
                instructions_changed = true;
                if ((col == 1 || (col == 3 && !val.equals(">>") && !val.equals("<<"))) && val.length() > 1 )
                    val = val.substring(0,1);

                instructions_list.get(row)[col] = val;

            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return true;
            }
        };


        instructions = new JTable(instructionsModel);

    }

    private void runMachine() {
        m.run();
        outputTextArea.setText(tape.toString());
    }

    private void runMachine(int i) {
        m.run(i);
        outputTextArea.setText(tape.toString());
    }

    private void setupMachine() {
        if (tape == null)
            resetMachineInput();

        instructions_changed = false;
        try {
            m.setInstructions(instructions_list);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(panel1, "Runtime exception: " + e.getMessage());
            instructions_changed = true;
        }
    }

    private void resetMachineInput() {
        tape = new GuiTape(inputTextArea.getText());
        m.setTape(tape);
        outputTextArea.setText(tape.toString());
        m.resetState();
    }

    private Language getLanguage() {
        String language_str = textField1.getText();
        if (language_str.isEmpty()) {
            JOptionPane.showMessageDialog(panel1, "First, set the language!");
            return null;
        }
        List<Character> language_lst = new LinkedList<Character>();
        for (char ch : language_str.toCharArray()) {
            language_lst.add(ch);
        }
        language_lst.add(Tape.START);
        language_lst.add(' ');

        return new LanguageImpl(language_lst);
    }

    public void updateSpeed() {
        boolean run_afterwards = false;
        if (timer.isRunning())
            run_afterwards = true;
        int min_delay = 1000/30;

        timer.stop();
        int delay = Math.max(min_delay, (int) (1000 / speed));
        timer = new Timer(delay, machine_operator);
        steps_per_frame = (int) ( min_delay / Math.min(min_delay, (1000 / speed)) );

        if (run_afterwards)
            timer.start();
    }
}

