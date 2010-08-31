package ui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import networks.*;
import structures.*;
import ui.*;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: Controller for the gui menu items</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (e-mail K.A.J.Doherty@herts.ac.uk
 * @version 1.0 First submission to R.G.Adams/N.Davey
 * @version 1.1 13 June 2005
 *              Add a File - New network menu item
 *              Add a Network - Speed menu item
 *              Created an OptionPane for a rate slider, and attempted
 *              to put some "intelligence" into the update rate maximum.
 *              But this is not perfect as we have a redraw performed on a
 *              timer object elsewhere
 * @version 1.2 14 June 2005
 *              Added reset() to call a menu reset, and a ui reset
 *              Added a cusor hourglass busy for the resets (as these
 *              take a long time)
 * @version 1.3 27 July 2005
 *              Added View Menu and view parameters
 * @version 1.4 28 July 2005
 *              Added Topology Measure
 * @version 1.5 Removed compiler > 1.5 container unchecked warnings
 *
 */

public class UserInterfaceController implements ActionListener, Observer {

    final private String credits = "Competitive Learning v1.2\nby\nKevin Doherty\n\nNovember 2007\n\nK.A.J.Doherty@herts.ac.uk";
    private UserInterfaceWindow ui;
    private UserInterfaceMenu menu;
    private application.Launcher model;
    private Algorithm algorithm;
    private JProgressBar monitor;
    private SelGraphics error;
    private Hashtable<Object,Object> parameters;
    private int totalIterations;

    public UserInterfaceController(Hashtable<Object,Object> parameters) {

        this.parameters = parameters;

        ui = new UserInterfaceWindow("Competitive Learning", this.parameters);
        makeMenu();
        ui.setVisible(true);


    }

    public void setModel(application.Launcher model) {
      this.model = model;
    }

    private void makeMenu() {
      menu = new UserInterfaceMenu(this, this.parameters);
      ui.setJMenuBar(menu);
    }

    public void reset() {
      menu.reset();
      ui.reset();
    }

    public void setBusy(boolean busy) {
      if (busy) {
        ui.setBusyCursor();
      } else {
        ui.setNormalCursor();
      }
    }



    public void configure(int totalIterations){

        // Add the progress bar
        if (totalIterations > 0) {

            this.totalIterations = totalIterations;

            monitor = new JProgressBar(0, totalIterations);
            monitor.setStringPainted(true);
            Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
            monitor.setBorder(BorderFactory.createTitledBorder(border,"Progress"));
            monitor.setPreferredSize(new java.awt.Dimension(100,50));
            ui.setTop(this.monitor);
        }

        // Add the error plot
        error = model.getErrorPlot();
        ui.setBottom(this.error, "Network Error (SSE)");

        // Add the network viewer
        algorithm = model.getAlgorithm();
        JComponent display = model.getDataView();
        if (display != null) {
            ui.setCenter(display);
        } else {
          System.err.print("Unable to set center display\n");
          System.exit(-3);
        }
        ui.invalidate();
    }


    /**
     * UserInterfaceController is an observer for the Counter class.
     * On counter change, we update the monitor progress bar.
     * @param subject Observable
     * @param arg Object
     */
    public void update(Observable subject, Object arg) {
      monitor.setValue( ((Counter) subject).getCounter() );
    }



    public void actionPerformed(ActionEvent e) {

      String command = e.getActionCommand();
      if (command == "About") {
        Runnable runnable = new Runnable() {
          public void run() {
            JOptionPane.showMessageDialog(ui, credits, "About", JOptionPane.INFORMATION_MESSAGE);
          }
        };
        SwingUtilities.invokeLater(runnable);
      } else if (command == "New") {

        // call init of configurationframe
        model.newConfigurationFrame();

      } else if (command == "Exit") {
        System.out.print("User requested shutdown\n");
        System.exit(1);
      } else if (command == "Network Window") {
        Object c = ui.getCenter();
        if (c instanceof DataView) {
          PrintUtilities printHelper = new PrintUtilities(((DataView)c).getPrintableArea());
          printHelper.print();
        } else {
          PrintUtilities printHelper = new PrintUtilities((JComponent)c);
          printHelper.print();
        }
      } else if (command == "Run") {
        if (algorithm.getState() == Algorithm.PAUSE) {
          algorithm.setState(Algorithm.RUN);
          // Grey out those items that are not valid
          menu.getRun().setEnabled(false);
          menu.getPause().setEnabled(true);
          menu.getStop().setEnabled(true);
        }
      } else if (command == "Pause") {
        if (algorithm.getState() == Algorithm.RUN) {
          algorithm.setState(Algorithm.PAUSE);
          // Grey out those items that are not valid
          menu.getRun().setEnabled(true);
          menu.getPause().setEnabled(false);
          menu.getStop().setEnabled(true);
        }
      } else if (command == "Stop") {
        algorithm.setState(Algorithm.STOP);
        // Grey out those items that are not valid
        menu.getRun().setEnabled(false);
        menu.getPause().setEnabled(false);
        menu.getStop().setEnabled(false);
      } else if (command == "Speed") {
        Runnable runnable = new Runnable() {
        public void run() {
          JOptionPane.showMessageDialog(ui,
                                        new ScreenUpdateRate(parameters, totalIterations),
                                        "Screen Update Rate (iterations)",
                                        JOptionPane.DEFAULT_OPTION);
          }
        };
        SwingUtilities.invokeLater(runnable);
      } else if (command == "Network Edges") {
        if (((AbstractButton)e.getSource()).getModel().isSelected())
          parameters.put("DISPLAY_NETWORK_EDGES",  "true");
        else
          parameters.put("DISPLAY_NETWORK_EDGES",  "false");

      } else if (command == "Delaunay Edges") {
        if (((AbstractButton)e.getSource()).getModel().isSelected())
          parameters.put("DISPLAY_INDUCED_DELAUNAY_EDGES", "true");
        else
          parameters.put("DISPLAY_INDUCED_DELAUNAY_EDGES", "false");
      } else if (command == "Topology Measure") {

        algorithm.setState( Algorithm.PAUSE );
        TopologyMeasurePanel p = new TopologyMeasurePanel(algorithm, parameters);
        double measure = p.getTopologyMeasure();

        String message ="Topological Correlation: "+measure+"\n"+
                        "SSE: "+algorithm.getSSE();
        JOptionPane.showMessageDialog(ui,
                                      message,
                                      "Topology Measure",
                                      JOptionPane.INFORMATION_MESSAGE);
        algorithm.setState( Algorithm.RUN );

      }

      // redraw the interface
      ui.invalidate();
    }
}


  /**
   *
   * <p>Title: Competitive Learning</p>
   * <p>Description: A suite of Competitve Learning Algorithms</p>
   * <p>Copyright: Copyright (c) 2005</p>
   * <p>Company: University of Hertfordshire</p>
   * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
   * @version 1.0 First release
   * @version 1.1 Added tick labels for iteration delay
   */
class ScreenUpdateRate extends JSlider {

    ScreenUpdateRate(final Hashtable<Object,Object> parameters, final int totalIterations) {
      super();
      // minimum display is 1
      final int min = 1;
      this.setMinimum(min);
      // maximum is much larger
      final int max = totalIterations / 10;
      this.setMaximum(max);
      Hashtable<Object,Object> labels = new Hashtable<Object,Object>();
      labels.put(new Integer(min), new JLabel(new Integer(min).toString()));
      labels.put(new Integer(max), new JLabel(new Integer(max).toString()));
      this.setLabelTable(labels);
      this.setPaintLabels(true);

      // set the display value of the slider to the
      // value to the current updaterate parameter
      if (parameters.get("DISPLAY_UPDATE_RATE") != null) {
        this.setValue(new Integer((String) parameters.get("DISPLAY_UPDATE_RATE")).
                      intValue());
      } else {
        this.setValue(1);
      }

      // add a change listener and do the listen on change stuff
      this.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          Object source = e.getSource();
          if (source instanceof ScreenUpdateRate) {
            JSlider slider = (JSlider)source;
            if (!slider.getValueIsAdjusting()) {
              parameters.put("DISPLAY_UPDATE_RATE",
                             (new Integer( slider.getValue())).toString() );
            }
          }
        }
      });
    }
}

