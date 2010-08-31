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

public class NG_panel extends AlgorithmPanel {

  public NG_panel() {
    super();
  }

  public void getParameters() {
    this.parameters.put("NG_NODES", jTextField1.getText());
    this.parameters.put("NG_NEIGHBOURHOOD_INITIAL", jTextField3.getText());
    this.parameters.put("NG_NEIGHBOURHOOD_FINAL", jTextField4.getText());
    this.parameters.put("NG_LEARNING_INITIAL", jTextField5.getText());
    this.parameters.put("NG_LEARNING_FINAL", jTextField6.getText());
  }


  protected void initialize() {

      // NG Size
      JLabel label1 = new JLabel();
      label1.setText("Number of Nodes:");
      label1.setBounds(new java.awt.Rectangle(10, 16, 120, 16));
      if (parameters.get("NG_NODES") != null) {
        jTextField1.setText((String) parameters.get("NG_NODES") );
      } else {
        jTextField1.setText( "" );
      }
      jTextField1.setBounds(new java.awt.Rectangle(170, 16, 55, 23));
      jTextField1.setToolTipText("Number of nodes (Integer > 0)");

      // Neighbourhood
      JLabel jLabel2 = new JLabel();
      jLabel2.setText("Neighbourhood: Initial");
      jLabel2.setBounds(new java.awt.Rectangle(10, 51, 160, 16));
      jLabel2.setToolTipText("Sets the neighbourhood function parameters");
      if (parameters.get("NG_NEIGHBOURHOOD_INITIAL") != null) {
        jTextField3.setText((String) parameters.get("NG_NEIGHBOURHOOD_INITIAL") );
      } else {
        jTextField3.setText( "" );
      }
      jTextField3.setBounds(new java.awt.Rectangle(170, 51, 55, 23));
      jTextField3.setToolTipText("Initial neighbourhood radius (Real > 0.0)");
      JLabel jLabel4 = new JLabel();
      jLabel4.setText("Final");
      jLabel4.setBounds(new java.awt.Rectangle(240, 51, 30, 16));
      jLabel4.setToolTipText("Final neighbourhood radius");
      if (parameters.get("NG_NEIGHBOURHOOD_FINAL") != null) {
        jTextField4.setText((String) parameters.get("NG_NEIGHBOURHOOD_FINAL") );
      } else {
        jTextField4.setText( "" );
      }
      jTextField4.setBounds(new java.awt.Rectangle(290, 51, 55, 23));
      jTextField4.setToolTipText("Final neighbourhood raduis (Real > 0.0)");


      // Learning Rate
      JLabel jLabel5 = new JLabel();
      jLabel5.setText("Learning Rate: Initial");
      jLabel5.setBounds(new java.awt.Rectangle(10, 84, 160, 16));
      jLabel5.setToolTipText("Network Learning Rate");
      JLabel jLabel7 = new JLabel();
      jLabel7.setText("Final");
      jLabel7.setBounds(new java.awt.Rectangle(240, 84, 30, 16));
      if (parameters.get("NG_LEARNING_INITIAL") != null) {
        jTextField5.setText((String) parameters.get("NG_LEARNING_INITIAL") );
      } else {
        jTextField5.setText( "" );
      }
      jTextField5.setBounds(new java.awt.Rectangle(170, 84, 55, 23));
      jTextField5.setToolTipText("Initial Learning Rate (Real > 0.0)");
      if (parameters.get("NG_LEARNING_FINAL") != null) {
        jTextField6.setText((String) parameters.get("NG_LEARNING_FINAL") );
      } else {
        jTextField6.setText( "" );
      }
      jTextField6.setBounds(new java.awt.Rectangle(290, 84, 55, 23));
      jTextField6.setToolTipText("Final Learning Rate (Real > 0.0)");

      add(label1);
      add(jTextField1);
      add(jLabel2);
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
