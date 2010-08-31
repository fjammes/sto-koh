package structures;

import java.util.Observable;

/**
 * <p>Title: Class Counter</p>
 * <p>Description: A simple Counter object for maintaining a counter.  An OO
 * approach to keeping a simple integer count variable.  Who said OO concepts
 * were miles off the path of simplicity and common sense?  And just to be
 * non-java or c, it starts at 1!/p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (e-mail K.A.J.Doherty@herts.ac.uk)
 * @version 1.0 Initial Release
 * @version 1.1 Now an Observable with toString()
 * @version 1.2 synchronized methods - makes bugger all difference
 */
public class Counter extends Observable {

    /**
     * Private scope int for maintaining the count
     * */
    private int counter;

    /**
     * No parameter constructor
     * */
    public Counter() {
        counter = 1;
    }

    /**
     * Getter for this count
     * @return int The current count
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Setter for this count
     * @param value The value to set this Counter to
     * @return int The revised counter value
     */
    synchronized public int setCounter(int value) {
        counter = value;
        // Observable
        setChanged();
        notifyObservers();
        return counter;
    }

    /**
     * Increment the count by 1
     * @return The incremented counter
     */
    synchronized public int increment() {
        counter++;
        // Observable
        setChanged();
        notifyObservers();
        return counter;
    }

    /**
     * Decrement the count by 1
     * @return The decremented counter
     */
    synchronized public int decrement() {
        counter--;
        // Observable
        setChanged();
        notifyObservers();
        return counter;
    }

    /**
     * Increment the count by the parameter increment
     * @param increment Increment
     * @return The incremented counter
     */
    synchronized public int incrementCounter(int increment) {
        counter += increment;
        // Observable
        setChanged();
        notifyObservers();
        return counter;
    }

    /**
     * Decrement the count by the parameter deccrement
     * @param decrement decrement
     * @return The decremented counter
     */
    synchronized public int decrementCounter(int decrement) {
        counter -= decrement;
        // Observable
        setChanged();
        notifyObservers();
        return counter;
    }

    /**
     * Overrides the Object toString() method
     * @return String Description of this Counter
     */
    public String toString() {
        return (new Integer(counter).toString());
    }
}
