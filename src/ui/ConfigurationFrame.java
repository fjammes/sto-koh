package ui;

import javax.swing.*;
import java.awt.*;
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
 * @version 1.0 First release
 * @version 1.1 14 June 2005
 * Minor changes - cancel button no longer exits application
 *               - run button calls the controller setup() method
 */

public class ConfigurationFrame extends ExitableJFrame {

  application.Launcher controller;
  Hashtable<Object,Object> parameters;

  // The algorithm specific parameter panel
  AlgorithmPanel algorithmController;
  // is placed in this container
  JPanel algorithmPanel = new JPanel();

  JTextField jTextFieldFileName = new JTextField();
  JTextField jTextFieldDimensionality = new JTextField();
  JTextField jTextFieldEpoch = new JTextField();
  JComboBox jComboBoxAlgorithm = new JComboBox();

  public ConfigurationFrame(String title,
                            application.Launcher controller,
                            Hashtable<Object,Object> parameters) {
    super(title);
    this.controller = controller;
    this.parameters = parameters;

    try {

      // Perform panel generation
      initialize();

      // and then display it
      this.setSize(480,400);
      this.setVisible(true);

      // Now make the display consistent with the
      // display of the dropdown - which is element(0)
      // at initial runtime
      for (Enumeration en = controller.getDropdownKeys(); en.hasMoreElements(); ){
        Object o = en.nextElement();
        if ( controller.getDropdownEntry(o).equals(jComboBoxAlgorithm.getModel().getElementAt(0))) {
          // Store the algorithm
          this.parameters.put("ALGORITHM",o);
          // Create the AlgorithmPanel associated with the default
          // dropdown element
          try {
            Class c = Class.forName((String) controller.getPanelEntry(o));
            algorithmController = (AlgorithmPanel) c.newInstance();
            algorithmController.setParamaters(this.parameters);
            algorithmPanel.add(algorithmController);
            algorithmPanel.repaint();
          }
          catch(ClassNotFoundException exception) {
            exception.printStackTrace();
          }
          catch(IllegalAccessException exception) {
            exception.printStackTrace();
          }
          catch(InstantiationException exception) {
            exception.printStackTrace();
          }
        }
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }


  }



  private void initialize() throws Exception {

    // Absolute positioning.
    // Life is just too short to use a gridbaglayout for all of this!
    this.getContentPane().setLayout(null);

    // Filename
    JLabel jLabelFilename = new JLabel();
    jLabelFilename.setText("Filename");
    jLabelFilename.setBounds(new Rectangle(30, 34, 105, 23));
    jTextFieldFileName.setText("");
    jTextFieldFileName.setBounds(new Rectangle(140, 34, 170, 23));
    this.getContentPane().add(jLabelFilename, null);
    this.getContentPane().add(jTextFieldFileName, null);

    // Filename selector
    JButton jButtonFileSelector = new JButton();
    jButtonFileSelector.setBounds(new java.awt.Rectangle(310, 34, 30, 23));
    jButtonFileSelector.setText("...");
    jButtonFileSelector.addActionListener(new
        ConfigurationFrame_jButtonFileSelector_actionAdapter(this));
    this.getContentPane().add(jButtonFileSelector, null);

    // Data Dimensionality and labelled
    JLabel jLabelDimensionality = new JLabel();
    jLabelDimensionality.setText("Dimensionality");
    jLabelDimensionality.setBounds(new Rectangle(30, 64, 105, 23));
    if (parameters.get("DEFAULT_DIMENSIONALITY") != null) {
      jTextFieldDimensionality.setText((String) parameters.get("DEFAULT_DIMENSIONALITY") );
    } else {
      jTextFieldDimensionality.setText( "" );
    }
    jTextFieldDimensionality.setBounds(new Rectangle(140, 64, 49, 23));
    jTextFieldDimensionality.setToolTipText("The data dimensionality (excluding label)(Integer >=1)");
    JCheckBox jCheckBoxLabelled = new JCheckBox();
    jCheckBoxLabelled.setText("Labelled");
    jCheckBoxLabelled.setBounds(new Rectangle(198, 64, 85, 23));
    parameters.put("LABELLED", new Boolean(false));
    jCheckBoxLabelled.addActionListener(new
        ConfigurationFrame_jCheckBoxLabelled_actionAdapter(this));

    // Epoch text box
    JLabel jLabelEpoch = new JLabel();
    jLabelEpoch.setText("Epoch");
    jLabelEpoch.setBounds(new java.awt.Rectangle(30, 96, 105, 23));
    jLabelEpoch.setToolTipText("Run network for n epochs");
    if (parameters.get("EPOCH") != null) {
      jTextFieldEpoch.setText((String) parameters.get("EPOCH") );
    } else {
      jTextFieldEpoch.setText( "" );
    }
    jTextFieldEpoch.setBounds(new java.awt.Rectangle(140, 96, 49, 23));
    jTextFieldEpoch.setToolTipText("Epoch count (Integer >=1)");

    // Algorithm Selector
    JLabel jLabelAlgorithm = new JLabel();
    jLabelAlgorithm.setText("Algorithm");
    jLabelAlgorithm.setBounds(new java.awt.Rectangle(30, 130, 105, 23));
    jComboBoxAlgorithm.setBounds(new Rectangle(140, 130, 220, 23));

    ArrayList<Object> list = new ArrayList<Object>();
    for (Enumeration e = controller.getDropdownKeys(); e.hasMoreElements(); ){
        list.add( controller.getDropdownEntry(  e.nextElement() ));
    }
    DefaultComboBoxModel comboboxmodel =
        new DefaultComboBoxModel(list.toArray());
    jComboBoxAlgorithm.setModel(comboboxmodel);
    jComboBoxAlgorithm.addActionListener(new
       ConfigurationFrame_jComboBoxAlgorithm_actionAdapter(this));

    // Container for the algorithm specific panels
    algorithmPanel.setBounds(new java.awt.Rectangle(31, 174, 400, 140));
    this.getContentPane().add(algorithmPanel, null);

    // Run
    JButton jButtonRun = new JButton();
    jButtonRun.setBounds(new Rectangle(146, 326, 100, 23));
    jButtonRun.setText("Configure");
    jButtonRun.addActionListener(new
        ConfigurationFrame_jButtonRun_actionAdapter(this));
    this.getContentPane().add(jButtonRun, null);

    // Cancel
    JButton jButtonCancel = new JButton();
    jButtonCancel.setBounds(new Rectangle(248, 326, 100, 23));
    jButtonCancel.setText("Cancel");
    jButtonCancel.addActionListener(new
        ConfigurationFrame_jButtonCancel_actionAdapter(this));
    this.getContentPane().add(jButtonCancel, null);
    this.getContentPane().add(jCheckBoxLabelled, null);
    this.getContentPane().add(jTextFieldDimensionality, null);
    this.getContentPane().add(jLabelDimensionality, null);
    this.getContentPane().add(jComboBoxAlgorithm, null);
    this.getContentPane().add(jLabelAlgorithm, null);
    this.getContentPane().add(jLabelEpoch);
    this.getContentPane().add(jTextFieldEpoch);
  }

  // Process the labelled checkbox
  void jCheckBoxLabelled_actionPerformed(ActionEvent e) {
    AbstractButton ab = (AbstractButton) e.getSource();
    parameters.put("LABELLED", new Boolean( ab.getModel().isSelected() ));
  }


  // Process the combobox algorithm drop down
  void jComboBoxAlgorithm_actionPerformed(ActionEvent e) {
    Object selected = jComboBoxAlgorithm.getSelectedItem();
    for (Enumeration en = controller.getDropdownKeys(); en.hasMoreElements(); ){
      Object o = en.nextElement();
      if ( controller.getDropdownEntry(o).equals(selected)) {
        // Store the algorithm
        parameters.put("ALGORITHM",o);
        try {
          Class c = Class.forName((String) controller.getPanelEntry(o));
          algorithmPanel.removeAll();
          algorithmController = (AlgorithmPanel) c.newInstance();
          algorithmController.setParamaters(parameters);
          algorithmPanel.add(algorithmController);
          algorithmPanel.repaint();
        }
        catch(ClassNotFoundException exception) {
          exception.printStackTrace();
	}
	catch(IllegalAccessException exception) {
          exception.printStackTrace();
        }
	catch(InstantiationException exception) {
            exception.printStackTrace();
	}
      }
    }
  }


  // RUN button actioned
  void jButtonRun_actionPerformed(ActionEvent e) {
    // here we store the algorithm specific parameters
    // in the parameters hashtable
    algorithmController.getParameters();

    boolean filenameok = !jTextFieldFileName.getText().equals("");
    boolean dimensionalityok = !jTextFieldDimensionality.getText().equals("");
    boolean epochok = !jTextFieldEpoch.getText().equals("");

    // and grab the final parameters from
    // the three textfields
    if ( filenameok && dimensionalityok && epochok) {

      parameters.put("FILE", jTextFieldFileName.getText());
      parameters.put("DIMENSIONALITY", jTextFieldDimensionality.getText());
      parameters.put("EPOCH", jTextFieldEpoch.getText());

      this.setVisible(false);
      this.dispose();

      // and pass it all back to the controller to do its stuff
      controller.setup();

    } else {

      Toolkit.getDefaultToolkit().beep();
      JOptionPane jop = null;
      if (!filenameok) {
        jop = new JOptionPane("Empty Filename (String pathname)",
                              JOptionPane.ERROR_MESSAGE);
      } else if (!dimensionalityok) {
        jop = new JOptionPane("Empty Dimensionality (Integer > 0)",
                              JOptionPane.ERROR_MESSAGE);
      } else if (!epochok) {
        jop = new JOptionPane("Empty Epoch (Integer > 0)",
                              JOptionPane.ERROR_MESSAGE);
      }
      JDialog dialog = jop.createDialog(this,"Error");
      dialog.setVisible(true);
    }
  }


  // CANCEL button actioned
  void jButtonCancel_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }


  // File selection button actioned
  void jButtonFileSelector_actionPerformed(ActionEvent e) {
    final JFileChooser fileChooser = new JFileChooser((String)parameters.get("DATA_DIRECTORY"));
    final int status = fileChooser.showOpenDialog(this);
    if (status == JFileChooser.APPROVE_OPTION) {
      String selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
      // write the filename in the textfield
      jTextFieldFileName.setText(selectedFile);
    } else if (status == JFileChooser.CANCEL_OPTION) {
      // don't do much - just close modal dialog
    }
  }

}


// Labelled checkbox actionlistener
class ConfigurationFrame_jCheckBoxLabelled_actionAdapter implements ActionListener {
  ConfigurationFrame adaptee;

  ConfigurationFrame_jCheckBoxLabelled_actionAdapter(ConfigurationFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jCheckBoxLabelled_actionPerformed(e);
  }
}


// RUN button actionlistener
class ConfigurationFrame_jButtonRun_actionAdapter implements ActionListener {
  ConfigurationFrame adaptee;

  ConfigurationFrame_jButtonRun_actionAdapter(ConfigurationFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonRun_actionPerformed(e);
  }
}


// CANCEL button actionlistener
class ConfigurationFrame_jButtonCancel_actionAdapter implements ActionListener {
  ConfigurationFrame adaptee;

  ConfigurationFrame_jButtonCancel_actionAdapter(ConfigurationFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonCancel_actionPerformed(e);
  }
}


// File button select actionlistener
class ConfigurationFrame_jButtonFileSelector_actionAdapter implements ActionListener {
  ConfigurationFrame adaptee;

  ConfigurationFrame_jButtonFileSelector_actionAdapter(ConfigurationFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonFileSelector_actionPerformed(e);
  }
}


// algorithm Combobox actionlistener
class ConfigurationFrame_jComboBoxAlgorithm_actionAdapter implements ActionListener {
  ConfigurationFrame adaptee;

  ConfigurationFrame_jComboBoxAlgorithm_actionAdapter(ConfigurationFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jComboBoxAlgorithm_actionPerformed(e);
  }
}

