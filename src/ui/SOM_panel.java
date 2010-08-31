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

public class SOM_panel extends AlgorithmPanel {

  public SOM_panel() {
    super();
  }

  public void getParameters() {
    this.parameters.put("SOM_ROWS", jTextField1.getText());
    this.parameters.put("SOM_COLUMNS", jTextField2.getText());
    this.parameters.put("SOM_NEIGHBOURHOOD_INITIAL", jTextField3.getText());
    this.parameters.put("SOM_NEIGHBOURHOOD_FINAL", jTextField4.getText());
    this.parameters.put("SOM_LEARNING_INITIAL", jTextField5.getText());
    this.parameters.put("SOM_LEARNING_FINAL", jTextField6.getText());
  }


  protected void initialize() {

      // SOM Size
      JLabel label1 = new JLabel();
      label1.setText("SOM Size:");
      label1.setBounds(new java.awt.Rectangle(10, 16, 150, 16));
      if (parameters.get("SOM_ROWS") != null) {
        jTextField1.setText((String) parameters.get("SOM_ROWS") );
      } else {
        jTextField1.setText( "" );
      }
      jTextField1.setBounds(new java.awt.Rectangle(160, 16, 55, 23));
      jTextField1.setToolTipText("Number of rows (Integer > 0)");
      JLabel jLabel1 = new JLabel();
      jLabel1.setText("by");
      jLabel1.setBounds(new java.awt.Rectangle(230, 16, 30, 16));
      if (parameters.get("SOM_COLUMNS") != null) {
        jTextField2.setText((String) parameters.get("SOM_COLUMNS") );
      } else {
        jTextField2.setText( "" );
      }
      jTextField2.setBounds(new java.awt.Rectangle(280, 16, 55, 23));
      jTextField2.setToolTipText("Number of columns (Integer > 0)");

      // Neighbourhood
      JLabel jLabel2 = new JLabel();
      jLabel2.setText("Neighbourhood: Initial");
      jLabel2.setBounds(new java.awt.Rectangle(10, 51, 150, 16));
      jLabel2.setToolTipText("Sets the initial neighbourhood function");
      if (parameters.get("SOM_NEIGHBOURHOOD_INITIAL") != null) {
        jTextField3.setText((String) parameters.get("SOM_NEIGHBOURHOOD_INITIAL") );
      } else {
        jTextField3.setText( "" );
      }
      jTextField3.setBounds(new java.awt.Rectangle(160, 51, 55, 23));
      jTextField3.setToolTipText("Value Real > 0.0");
      JLabel jLabel4 = new JLabel();
      jLabel4.setText("Final");
      jLabel4.setBounds(new java.awt.Rectangle(230, 51, 30, 16));
      jLabel4.setToolTipText("Final neighbourhood function");
      if (parameters.get("SOM_NEIGHBOURHOOD_INITIAL") != null) {
        jTextField4.setText((String) parameters.get("SOM_NEIGHBOURHOOD_FINAL") );
      } else {
        jTextField4.setText( "" );
      }
      jTextField4.setBounds(new java.awt.Rectangle(280, 51, 55, 23));
      jTextField4.setToolTipText("0.0 < Value < 0.1");


      // Learning Rate
      JLabel jLabel5 = new JLabel();
      jLabel5.setText("Learning Rate: Initial");
      jLabel5.setBounds(new java.awt.Rectangle(10, 84, 150, 16));
      jLabel5.setToolTipText("Network Learning Rate");
      if (parameters.get("SOM_LEARNING_INITIAL") != null) {
        jTextField5.setText((String) parameters.get("SOM_LEARNING_INITIAL") );
      } else {
        jTextField5.setText( "" );
      }
      jTextField5.setBounds(new java.awt.Rectangle(160, 84, 55, 23));
      jTextField5.setToolTipText("Initial Learning Rate (Real > 0.0)");
      JLabel jLabel7 = new JLabel();
      jLabel7.setText("Final");
      jLabel7.setBounds(new java.awt.Rectangle(230, 84, 45, 16));
      if (parameters.get("SOM_LEARNING_FINAL") != null) {
        jTextField6.setText((String) parameters.get("SOM_LEARNING_FINAL") );
      } else {
        jTextField6.setText( "" );
      }
      jTextField6.setBounds(new java.awt.Rectangle(280, 84, 55, 23));
      jTextField6.setToolTipText("Final Learning Rate (Real > 0.0)");

      add(label1);
      add(jTextField1);
      add(jLabel1);
      add(jTextField2);
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
  private JTextField jTextField2 = new JTextField();
  private JTextField jTextField3 = new JTextField();
  private JTextField jTextField4 = new JTextField();
  private JTextField jTextField5 = new JTextField();
  private JTextField jTextField6 = new JTextField();
}
