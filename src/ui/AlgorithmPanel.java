package ui;

import java.awt.*;
import javax.swing.*;
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

abstract public class AlgorithmPanel extends JPanel {

    public Hashtable<Object,Object> parameters;

    public AlgorithmPanel() {
      super();
      this.setLayout(null);
      this.setSize(new java.awt.Dimension(400, 140));
      this.setBorder(BorderFactory.createLineBorder(Color.black));
    }


    public void setParamaters(Hashtable<Object,Object> parameters) {
      this.parameters = parameters;
      initialize();
    }

    abstract public void getParameters();

    abstract protected void initialize();
}
