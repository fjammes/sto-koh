package ui;

import javax.swing.*;
import java.awt.event.*;
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
 * @version 1.0 First submission to R.G.Adams/N.Davey
 * @version 1.1 13 June 2005
 *              Add a File - New network menu item
 *              Add a Network - Speed menu item
 *              Added reset() method to set algorithm state
 *              to a useful position
 * @version 1.2 27 July 2005
 *              Added View Menu and view parameters
 * @version 1.3 Removed compiler > 1.5 container unchecked warnings
 
 */

public class UserInterfaceMenu extends JMenuBar {

    final private ActionListener menuListener;

    final private JMenuItem menuItemRun = new JMenuItem("Run");
    final private JMenuItem menuItemPause = new JMenuItem("Pause");
    final private JMenuItem menuItemStop = new JMenuItem("Stop");

    private Hashtable parameters;



    public UserInterfaceMenu(ActionListener uic, Hashtable parameters) {
        super();
        menuListener = uic;
        this.parameters = parameters;

        try {
            initialise();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    final public void reset() {
      menuItemRun.setEnabled(true);
      menuItemPause.setEnabled(false);
      menuItemStop.setEnabled(false);
    }


    final private void initialise() throws Exception {
      JMenu fileMenu = new JMenu("File");
      JMenu networkMenu = new JMenu("Network");
      JMenu viewMenu = new JMenu("View");
      JMenu printMenu = new JMenu("Print");
      JMenu helpMenu = new JMenu("Help");
      JMenuItem menuItemNew = new JMenuItem("New");
      JMenuItem menuItemExit = new JMenuItem("Exit");
      JMenuItem menuItemSpeed = new JMenuItem("Speed");

      boolean displayGraphEdges =
          new Boolean((String)parameters.get("DISPLAY_NETWORK_EDGES")).booleanValue();
      JCheckBoxMenuItem menuItemGraph = new JCheckBoxMenuItem("Network Edges", displayGraphEdges);
      
      boolean displayInducedDelaunayEdges =
          new Boolean((String)parameters.get("DISPLAY_INDUCED_DELAUNAY_EDGES")).booleanValue();
      JCheckBoxMenuItem menuItemDelaunay = new JCheckBoxMenuItem("Delaunay Edges", displayInducedDelaunayEdges);

      JMenuItem menuItemTopologyMeasure = new JMenuItem("Topology Measure");

      JMenuItem menuItemPrintNetwork = new JMenuItem("Network Window");
      JMenuItem menuItemAbout = new JMenuItem("About");
      this.add(fileMenu);
      this.add(networkMenu);
      this.add(viewMenu);
      this.add(printMenu);
      this.add(helpMenu);
      // FILE menu
      fileMenu.add(menuItemNew);
      menuItemNew.addActionListener(menuListener);
      fileMenu.add(menuItemExit);
      menuItemExit.addActionListener(menuListener);

      // NETWORK menu
      // --> Run
      networkMenu.add(menuItemRun);
      menuItemRun.addActionListener(menuListener);
      menuItemRun.setEnabled(false);

      // --> Pause
      networkMenu.add(menuItemPause);
      menuItemPause.addActionListener(menuListener);
      menuItemPause.setEnabled(false);

      // --> Stop
      networkMenu.add(menuItemStop);
      menuItemStop.addActionListener(menuListener);
      menuItemStop.setEnabled(false);

      // --> Speed
      networkMenu.add(new JSeparator());
      networkMenu.add(menuItemSpeed);
      menuItemSpeed.addActionListener(menuListener);


      // VIEW menu
      // --> Network Edges
      viewMenu.add(menuItemGraph);
      menuItemGraph.addActionListener(menuListener);

      // --> Delaunay Edges
      viewMenu.add(menuItemDelaunay);
      menuItemDelaunay.addActionListener(menuListener);

      // --> Topology Measure
      viewMenu.add(new JSeparator());
      viewMenu.add(menuItemTopologyMeasure);
      menuItemTopologyMeasure.addActionListener(menuListener);


      // PRINT menu
      printMenu.add(menuItemPrintNetwork);
      menuItemPrintNetwork.addActionListener(menuListener);


      // HELP menu
      helpMenu.add(menuItemAbout);
      menuItemAbout.addActionListener(menuListener);
  }

  // Getter for the Run menu item
  // for enable/disable from controller
  final public JMenuItem getRun() {
    return menuItemRun;
  }

  // Getter for the Pause menu item
  // for enable/disable from controller
  final public JMenuItem getPause() {
    return menuItemPause;
  }

  // Getter for the Stop menu item
  // for enable/disable from controller
  final public JMenuItem getStop() {
    return menuItemStop;
  }

}
