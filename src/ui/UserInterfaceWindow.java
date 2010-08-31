package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.util.*;

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


final public class UserInterfaceWindow extends ExitableJFrame {

    // This user interface is essentailly a border layout, with north
    // being a progress bar, center being a network viewer, and south
    // being an error plot.
    private JComponent center;
    private JComponent top;
    private JComponent bottom;


    // This windows cursors
    final private Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);
    final private Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);


    // This is a reference to the global parameters table that we pass
    // in as a parameter
    private Hashtable parameters;


    /**
     * Basic exitable JFrame constructor
     * @param title String
     * @param parameters Hashtable
     */
    public UserInterfaceWindow(String title, Hashtable parameters) {
        super(title);
        this.parameters = parameters;
        setScreen();
        this.addWindowListener(new GenericWindowAdapter());
    }


    /** Set the screen size and position */
    private void setScreen() {
      if (parameters.containsKey("SCREEN_WIDTH") && parameters.containsKey("SCREEN_HEIGHT")) {
        this.setSize(new Integer((String) parameters.get("SCREEN_WIDTH")).intValue(),
                     new Integer((String) parameters.get("SCREEN_HEIGHT")).intValue());
      } else {
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int height = (8 * screen.height) / 9;
        final int width = (8 * screen.width) / 9;
        this.setSize(width, height);
      }
    }


    /**
     * Add a menubar to this window
     * @param menu JMenuBar
     */
    public void setMenu(JMenuBar menu) {
        this.setJMenuBar(menu);
    }


    /**
     * Set the top left window to this component
     * @param component JComponent
     */
    public void setCenter(JComponent component) {
        center = component;
        getContentPane().add(center, BorderLayout.CENTER);
        this.validate();
    }


    /**
     * Set the bottom of this window to this component
     * @param component JComponent
     */
    public void setTop(JComponent component) {
        top = component;
        getContentPane().add(top, BorderLayout.NORTH);
        this.validate();
    }

    /**
     * Set the bottom of this window to this component
     * @param component JComponent
     * @param title String
     */
    public void setBottom(JComponent component, String title) {
        bottom = component;

        // Place the component on a raise bevel border
        JPanel panel = new JPanel();
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setMinimumSize(new Dimension(200,80));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(BevelBorder.RAISED),
            title));
        getContentPane().add(scroll, BorderLayout.SOUTH);

        panel.add(bottom, BorderLayout.CENTER);

        this.validate();
    }



    public void reset() {
      getContentPane().removeAll();
    }


    /**
     * getter for the data view component
     * @return JComponent
     */
    public JComponent getCenter() {
      return center;
    }


    /**
     * getter for the bottom error component
     * @return JComponent
     */
    public JComponent getBottom() {
      return bottom;
    }

    /**
     * getter for the bottom progress component
     * @return JComponent
     */
    public JComponent getTop() {
      return top;
    }


    /**
     * Set this frame to indicate a busy state
     */
    public void setBusyCursor() {
      setCursor(busyCursor);
    }


    /**
     * Set this frame to indicate a normal state
     */
    public void setNormalCursor() {
      setCursor(normalCursor);
    }
}
