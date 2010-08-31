package ui;

import javax.swing.*;
import java.awt.*;
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

public class CL_panel extends AlgorithmPanel {

    public CL_panel() {
      super();
    }


    public void getParameters(){
      this.parameters.put("CL_NODES", jTextField1.getText());
      this.parameters.put("CL_LEARNING_RATE", jTextField2.getText());
    }


    protected void initialize() {

      // Network Size
      JLabel jLabel1 = new JLabel();
      jLabel1.setText("Number of Nodes:");
      jLabel1.setBounds(new java.awt.Rectangle(10, 16, 120, 16));
      if (parameters.get("CL_NODES") != null) {
        jTextField1.setText((String) parameters.get("CL_NODES") );
      } else {
        jTextField1.setText( "" );
      }
      jTextField1.setBounds(new java.awt.Rectangle(160, 16, 55, 23));
      jTextField1.setToolTipText("Number of nodes (Integer > 0)");

      // Learning Rate
      JLabel jLabel2 = new JLabel();
      jLabel2.setText("Learning Rate:");
      jLabel2.setBounds(new java.awt.Rectangle(10, 51, 120, 16));
      jLabel2.setToolTipText("Sets the learning rate");
      if (parameters.get("CL_LEARNING_RATE") != null) {
        jTextField2.setText((String) parameters.get("CL_LEARNING_RATE") );
      } else {
        jTextField2.setText( "" );
      }
      jTextField2.setBounds(new java.awt.Rectangle(160, 51, 55, 23));
      jTextField2.setToolTipText("Learning Rate (0.0 < Real <=1.0)");
      add(jLabel1);
      add(jTextField1);
      add(jLabel2);
      add(jTextField2);
    }


    private JTextField jTextField1 = new JTextField();
    private JTextField jTextField2 = new JTextField();

}
