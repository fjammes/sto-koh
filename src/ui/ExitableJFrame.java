package ui;

import java.awt.event.*;
import javax.swing.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: A suite of Competitve Learning Algorithms</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public class ExitableJFrame extends JFrame {
    public static final int EXIT_ON_CLOSE = JFrame.EXIT_ON_CLOSE;
    private int returnCode = 0;

    public ExitableJFrame() {
        init();
    }

    public ExitableJFrame(String title) {
        super(title);
        init();
    }

    private void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void setReturnCode(int newValue) {
        returnCode = newValue;
    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        int defaultCloseOperation = getDefaultCloseOperation();
        if ((e.getID() == WindowEvent.WINDOW_CLOSING) && (defaultCloseOperation == EXIT_ON_CLOSE)) {
            System.exit(returnCode);
        }
    }

    protected String paramString() {
        String returnValue = "";
        int defaultCloseOperation = getDefaultCloseOperation();
        if (defaultCloseOperation == EXIT_ON_CLOSE) {
            returnValue = ",EXIT_ON_CLOSE";
        }
        return super.paramString() + returnValue;
    }
}
