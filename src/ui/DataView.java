package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import structures.*;
import java.util.*;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: Dimension Switching View</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 * @version 1.2 Removed compiler > 1.5 container unchecked warnings
 */
public class DataView extends JPanel {

    private InputGraphicObserver o;
    private JComponent printableArea;

    /**
     * Provides a wrapper around an InputGraphicObserver class, for the display of the Observable and static input data
     * @param dimensions int
     * @param parameters Hashtable
     * @param data Vertex[]
     */
    public DataView(Vertex[] data, int dimensions, Hashtable parameters) {
        super();
        this.setLayout(new BorderLayout());
        Border border = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        this.setBorder(border);
        o = new InputGraphicObserver(data, parameters);
        // this is the printable area
        this.setPrintableArea(o);
        // Add display and controls to this panel
        this.add(o, BorderLayout.CENTER);
//        I can not get these controls on a pane of greater
//        priority than the sse painted error graph
//        this.add(createViewControls(dimensions), BorderLayout.SOUTH);

        // Try a repaint timer
        ActionListener actionListener = new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            o.trypaint(null,null);
          }
        };
        javax.swing.Timer timer = new javax.swing.Timer(5000, actionListener);
        timer.start();
    }


    final public void update(Graph network, Graph delaunay) {
      o.trypaint(network, delaunay);
    }

    /**
     * The InputGraphicObserver this is a data view wrapper for
     * @return InputGraphicObserver
     */
    final public InputGraphicObserver getInputGraphicObserver() {
      return o;
    }

    /**
     * Get the printable area
     * @return JComponent
     */
    final public JComponent getPrintableArea() {
      return printableArea;
    }

    /**
     * Set the printable area
     * @param c JComponent
     */
    final public void setPrintableArea(JComponent c) {
      printableArea = c;
    }

    /**
     * Create the axis switching drop downs and provide the functionality with an appropriate ActionListener
     * @param dim int
     * @return JPanel
     */
    final JPanel createViewControls(int dim) {

        JPanel p = new JPanel(new GridLayout(1, 2));
        p.setBackground( Color.WHITE );
        // Populate the contents of the axis JComboBoxes
        String[] element = new String[dim];
        for (int i = 0; i < dim; i++) {
            element[i] = new String(new Integer(i).toString());
        }
        // Dimension 0 Control
        JComboBox dim0 = new JComboBox(element);
        dim0.setSelectedIndex(o.getX0());
        ActionListener actionListenerDim0 = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ItemSelectable is = (ItemSelectable)ae.getSource();
                getInputGraphicObserver().setX0(Integer.parseInt(selectedString(is)));
            }
        };
        dim0.addActionListener(actionListenerDim0);
        JPanel x0panel = new JPanel();
        x0panel.setBackground( Color.WHITE );
        JLabel x0text = new JLabel("X0:");
        x0panel.add(x0text);
        x0panel.add(dim0);
        p.add(x0panel);

        // Dimension 1 Control
        JComboBox dim1 = new JComboBox(element);
        dim1.setSelectedIndex(o.getX1());
        ActionListener actionListenerDim1 = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ItemSelectable is = (ItemSelectable)ae.getSource();
                getInputGraphicObserver().setX1(Integer.parseInt(selectedString(is)));
            }
        };
        dim1.addActionListener(actionListenerDim1);
        JPanel x1panel = new JPanel();
        x1panel.setBackground( Color.WHITE );
        JLabel x1text = new JLabel("X1:");
        x1panel.add(x1text);
        x1panel.add(dim1);
        p.add(x1panel);
        // return the panel
        return p;
    }

    /**
     * JComboBox returns an ItemSelectable, so return the string of the first item selected
     * @param is ItemSelectable
     * @return String
     */
    private String selectedString(ItemSelectable is) {
        Object[] selected = is.getSelectedObjects();
        return ((selected.length == 0) ? "null" : (String)selected[0]);
    }
}
