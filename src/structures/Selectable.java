package structures;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: Selectable Interface for selectable Objects</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (e-mail K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */
public interface Selectable {
    boolean getSelected();

    void setSelected(boolean state);
}
