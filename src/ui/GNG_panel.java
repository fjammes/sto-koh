package ui;

import javax.swing.*;
import java.awt.*;

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

public class GNG_panel extends AlgorithmPanel {

  public GNG_panel() {
    super();
  }


  public void getParameters() {
    this.parameters.put("GNG_NODES", jTextField1.getText());
    this.parameters.put("GNG_NODE_INSERTION", jTextField3.getText());
    this.parameters.put("GNG_EDGE_DELETION", jTextField4.getText());
    this.parameters.put("GNG_BMU_LEARNING_RATE", jTextField5.getText());
    this.parameters.put("GNG_NEIGHBOUR_LEARNING_RATE", jTextField6.getText());
  }


  protected void initialize() {

      // Network Size
      JLabel label1 = new JLabel();
      label1.setText("Number of Nodes:");
      label1.setBounds(new java.awt.Rectangle(10, 21, 150, 16));
      if (parameters.get("GNG_NODES") != null) {
        jTextField1.setText((String) parameters.get("GNG_NODES") );
      } else {
        jTextField1.setText( "" );
      }
      jTextField1.setBounds(new java.awt.Rectangle(160, 21, 55, 23));
      jTextField1.setToolTipText("Number of Nodes (Integer >= 2)");

      // Network modification parameters
      JLabel jLabel3 = new JLabel();
      jLabel3.setText("Age: Node Insertion");
      jLabel3.setBounds(new java.awt.Rectangle(10, 52, 150, 16));
      jLabel3.setToolTipText("Iteration count before new node insertion (Integer >= 1)");
      if (parameters.get("GNG_NODE_INSERTION") != null) {
        jTextField3.setText((String) parameters.get("GNG_NODE_INSERTION") );
      } else {
        jTextField3.setText( "" );
      }
      jTextField3.setBounds(new java.awt.Rectangle(160, 52, 55, 23));
      jTextField3.setToolTipText("Network Growth Age (Integer > 1)");
      JLabel jLabel4 = new JLabel();
      jLabel4.setText("Edge Deletion");
      jLabel4.setBounds(new java.awt.Rectangle(225, 52, 100, 16));
      jLabel4.setToolTipText("Edge deletion age (Integer > 1)");
      if (parameters.get("GNG_EDGE_DELETION") != null) {
        jTextField4.setText((String) parameters.get("GNG_EDGE_DELETION") );
      } else {
        jTextField4.setText( "" );
      }
      jTextField4.setBounds(new java.awt.Rectangle(325, 52, 55, 23));
      jTextField4.setToolTipText("Edge Deletion Age (Integer > 1)");


      // Learning Rates
      JLabel jLabel5 = new JLabel();
      jLabel5.setText("Learning Rate: BMU");
      jLabel5.setBounds(new java.awt.Rectangle(10, 84, 150, 16));
      jLabel5.setToolTipText("Network Learning Rate");
      JLabel jLabel7 = new JLabel();
      jLabel7.setText("Neighbourhood");
      jLabel7.setBounds(new java.awt.Rectangle(225, 84, 100, 16));
      if (parameters.get("GNG_BMU_LEARNING_RATE") != null) {
        jTextField5.setText((String) parameters.get("GNG_BMU_LEARNING_RATE") );
      } else {
        jTextField5.setText( "" );
      }
      jTextField5.setBounds(new java.awt.Rectangle(160, 84, 55, 23));
      jTextField5.setToolTipText("BMU Learning Rate (Real > 0.0)");
      if (parameters.get("GNG_NEIGHBOUR_LEARNING_RATE") != null) {
        jTextField6.setText((String) parameters.get("GNG_NEIGHBOUR_LEARNING_RATE") );
      } else {
        jTextField6.setText( "" );
      }
      jTextField6.setBounds(new java.awt.Rectangle(325, 84, 55, 23));
      jTextField6.setToolTipText("Neighbourhood Learning Rate (Real > 0.0)");

      add(label1);
      add(jTextField1);
      add(jLabel3);
      add(jTextField3);
      add(jLabel4);
      add(jTextField4);
      add(jLabel5);
      add(jLabel7);
      add(jTextField5);
      add(jTextField6);

  }


  private JTextField jTextField1 = new JTextField();
  private JTextField jTextField3 = new JTextField();
  private JTextField jTextField4 = new JTextField();
  private JTextField jTextField5 = new JTextField();
  private JTextField jTextField6 = new JTextField();
}
