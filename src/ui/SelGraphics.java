package ui;

// ========================================================================== ;
//                                                                            ;
//     Copyright (1996-1998) Hartmut S. Loos                                  ;
//                                                                            ;
//     Institut f"ur Neuroinformatik   ND 03                                  ;
//     Ruhr-Universit"at Bochum                                               ;
//     44780 Bochum                                                           ;
//                                                                            ;
//     Tel  : +49 234 7007845                                                 ;
//     Email: Hartmut.Loos@neuroinformatik.ruhr-uni-bochum.de                 ;
//                                                                            ;
//     For version information and parameter explanation have a look at       ;
//     the file 'DemoGNG.java'.                                               ;
//                                                                            ;
// ========================================================================== ;
//                                                                            ;
// This class was originally written by                                       ;
// Christian Kurzke (cnkurzke@cip.informatik.uni-erlangen.de) and             ;
// Ningsui Chen (nichen@cip.informatik.uni-erlangen.de).                      ;
// The original version of the SelGraphics class is included in a Java        ;
// applet at http://www2.informatik.uni-erlangen.de                           ;
//         /IMMD-II/Persons/jacob/Evolvica/Java/CharacterEvolution/index.html ;
// The applet shows an Evolutionary Algorithm (very impressive!).             ;
//                                                                            ;
// I have only made minor changes to this class. Many thanks to Christian     ;
// Kurzke and Ningsui Chen for this nice graph class.                         ;
//                                                                            ;
// ========================================================================== ;
//                                                                            ;
// Copyright 1996-1998  Hartmut S. Loos                                       ;
//                                                                            ;
// This program is free software; you can redistribute it and/or modify       ;
// it under the terms of the GNU General Public License as published by       ;
// the Free Software Foundation; either version 1, or (at your option)        ;
// any later version.                                                         ;
//                                                                            ;
// This program is distributed in the hope that it will be useful,            ;
// but WITHOUT ANY WARRANTY; without even the implied warranty of             ;
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              ;
// GNU General Public License for more details.                               ;
//                                                                            ;
// You should have received a copy of the GNU General Public License          ;
// along with this program; if not, write to the Free Software                ;
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.                  ;
//                                                                            ;
// ========================================================================== ;
//
//   08 June 2005 - Used with thanks
//   Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
//   SelGraphics is now a JPanel, not a Canvas; with its own
//   repaint timer for display management.
//   Minor changes to put title outside grid, removed x axis ticks and
//   vertical lines, added a maximum value to the y-axis, changed
//   some colors and removed some superfluous stuff
//   14 June 2005 - Following feedback from R.Adams and N.Davey
//   Suppress the exception caused by the timer when trying
//   to paint itself from the timer event when this is null!
//
//   23 November 2007 - removed the nested Vector of Vectors for the
//   data storage.
//   Added <Generics> to silence the compiler > 1.5 bitching about 
//   unsafe casts, etc.
//   Removed some of the clunky draw code
//   Removed "duplicate" draw code in add() method
//   Added dirty to stop flicker when network stopped
// ==========================================================================

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Generates a graph.
 * It scales its axis automatically, so the window is always
 * filled at its maximum.
 *
 */
public class SelGraphics extends JPanel {
  /**
   * The initial maximum value for the x-axis.
   */
  final int INIT_MAX_X = 5;
  /**
   * The initial maximum value for the y-axis
   */
  int INIT_MAX_Y = 1;

  Graphics g;
  Vector<Double>  data;

  boolean dirty = true;  // Only repaint when data is dirty
  
  java.text.NumberFormat formatter = new java.text.DecimalFormat("0.0");
  double maxInput = 0.1; // The maximum input seen so far

  int ScaleMaxX = INIT_MAX_X;
  int ScaleMaxY = INIT_MAX_Y;

  /**
   * The constructor.
   *
   */
  public SelGraphics(String s, int y) {
    super();

    // The initial size of the frame
    INIT_MAX_Y = y;
    ScaleMaxY = INIT_MAX_Y;
    this.setPreferredSize(new Dimension(400,120));
    data = new Vector<Double>(1000);
    this.setVisible(true);

    // Repaint timer
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        if (dirty) trypaint();
      }
    };
    javax.swing.Timer timer = new javax.swing.Timer(500, actionListener);
    timer.start();

  }


  // call the paint method, and suppress
  // the exception caused when this is null!
  private void trypaint() {
    try {
      paint(this.getGraphics());
    }
    catch (Exception e){}
  }


  // public accessor to paintcomponent
  public void paintComponents(Graphics g) {
    paint(this.getGraphics());
  }


  /**
   * Draws the chart on the screen.
   *
   * @param g	The drawing area
   */
  public void paint(Graphics g) {
    int i = 0;
    int lx = 30;
    int ly = 30;
    double sx,sy;
    Graphics da;
    Rectangle size;
    Rectangle Diagram;

    // Mark the graphic as clean, so don't repaint
    dirty = false;

    // get bounds
    da = this.getGraphics();
    size = this.getBounds();
    Diagram = new Rectangle(40, 5, size.width - 80, size.height - 25);

    // Overdraw any graph overflow on background
    da.setColor( this.getBackground() );
    da.fillRect(0, 0, size.width, size.height);
    da.setColor( Color.BLACK );
    da.drawString(formatter.format(maxInput),8,16);

    // paint drawing aera
    da.translate(Diagram.x, Diagram.y);
    da.setColor( SystemColor.controlLtHighlight );
    da.fillRect(0, 0, Diagram.width, Diagram.height);

    // paint axis
    da.setColor( Color.BLACK );
    da.drawLine(0, 0, 0, Diagram.height);
    da.drawLine(0, Diagram.height, Diagram.width, Diagram.height);

    sx = ((double) Diagram.width) / ScaleMaxX;
    sy = ((double) Diagram.height) / ScaleMaxY;

    // paint traces
    da.setColor( Color.BLACK );
    
    int nx, ny, ox = -1, oy = -1;
    
    Iterator iter = data.iterator();
    while(iter.hasNext()) {
        nx = (int)(sx * i++);
        ny = Diagram.height - (int)(((Double)iter.next()).doubleValue() * sy);
        if (oy > 0) da.drawLine(ox, oy, nx, ny);
        ox = nx;
        oy = ny;
     }

  }

  /**
   * Add the data to the graph.
   *
   * @param value            The new value for the graph
   */
  public void add(double value) {

    dirty = true;  // Mark the graphic as dirty to force a repaint
	  
    if (value > maxInput) maxInput = value;

    data.addElement( new Double(value) );

    if ( value > ScaleMaxY) {
      ScaleMaxY = (int)(1.2 * value);
      trypaint();
    }

    if (data.size() > ScaleMaxX) {
      ScaleMaxX = data.size() + 20;
      trypaint();
    }

  }

}
