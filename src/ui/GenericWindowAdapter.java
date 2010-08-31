package ui;

/* Generic Window Adapter Class
   Written by Kely Zorgdrager
   http://www.developer.ibm.com/library/articles/programmer/kelby1.html
   Used with many thanks
 */

import java.awt.*;
import java.awt.event.*;

public class GenericWindowAdapter extends WindowAdapter {

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
        Object source = e.getSource();
        if (source instanceof Window) {
            ((Window)source).repaint();
        }
    }

    public void windowClosing(WindowEvent e) {
        Object source = e.getSource();
        if (source instanceof Window) {
            ((Window)source).setVisible(false);
            ((Window)source).dispose();
        }
    }

    public void windowClosed(WindowEvent e) {
        Object source = e.getSource();
        if (source instanceof Window) {
            System.exit(0);
        }
    }
}
