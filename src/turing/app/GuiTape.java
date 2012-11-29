package turing.app;

import turing.TapeImpl;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 29/11/2012
 * Time: 00:16
 */
public class GuiTape extends TapeImpl {
    private int output_width = 200;

    public GuiTape(String tape_string) {
        super(tape_string);
    }

    public void setOutputWidth(int output_width) {
        this.output_width = output_width;
    }

    @Override
    public String toString() {
        // This formats TapeImpl.toString() as a wrapped string,
        // with a ^ under the pointer
        String total_string = super.toString();

        StringBuilder sbuf = new StringBuilder();
        int pos = getPosition();
        int string_length = total_string.length();

        for (int i=0; i<total_string.length(); i += output_width) {
            sbuf.append(total_string.substring(i, Math.min(i + output_width, string_length)));
            sbuf.append('\n');

            if (pos >= i && pos < i + output_width) {
                for (int j=0; j<pos-i; ++j) {
                    sbuf.append('_');
                }
                sbuf.append("^\n");
            }
        }
        return sbuf.toString();
    }
}
