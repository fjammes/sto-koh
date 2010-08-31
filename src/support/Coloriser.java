package support;

import java.awt.*;

/**
 * <p>Title: Coloriser</p>
 * <p>Description: creates a fixed color palette</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: U&niversity of Hertfordshire</p>
 * @author K.A.J.Doherty@herts.ac.uk
 * @version 1.0
 */
public class Coloriser {
    final int MAXCOLOR = 255 * 255;
    final private Color[] classColor = new Color[MAXCOLOR];

    public Coloriser() {
        // define sensible default low index values
        classColor[0] = new Color(1.0f, 0.0f, 0.0f);
        classColor[1] = new Color(0.0f, 1.0f, 0.0f);
        classColor[2] = new Color(0.0f, 0.0f, 1.0f);
        classColor[3] = new Color(1.0f, 1.0f, 0.0f);
        classColor[4] = new Color(0.0f, 1.0f, 1.0f);
        classColor[5] = new Color(1.0f, 0.0f, 1.0f);
        classColor[6] = new Color(1.0f, 0.5f, 0.0f);
        classColor[7] = new Color(0.0f, 1.0f, 0.5f);
        classColor[8] = new Color(1.0f, 0.0f, 0.5f);
        classColor[9] = new Color(0.0f, 0.5f, 0.0f);
        classColor[10] = new Color(0.0f, 0.5f, 1.0f);
        classColor[11] = new Color(0.5f, 0.0f, 1.0f);
        for (int c = 12; c < MAXCOLOR; c++) {
            classColor[c] = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
        }
    }

    /**
     * get the color assigned to the index value
     * @param index Int index into the color palette
     * @return Color from the palette
     */
    final public Color getColor(int index) {
        if (index < MAXCOLOR) {
            return classColor[index];
        } else {
            System.err.println("Coloriser: Requested color out of bounds");
            return classColor[0];
        }
    }
}
