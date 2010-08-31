package networks;

import java.util.*;
import structures.*;

/**
 *
 * <p>Title: Competitive Learning</p>
 *
 * <p>Description: An error table - essentially a hashmap of vertex and errors</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: University of Hertfordshire</p>
 *
 * @author Kevin Doherty (K.A.J.Doherty@herts.ac.uk)
 * @version 1.0
 */

public class ErrorTable {
    /** Storage for the error Entry objects */
    private TreeSet<Entry> errors;

    /** No parameter constructor */
    public ErrorTable() {
        errors = new TreeSet<Entry>();
    }

    /** remove all the entries from the error entry storage */
    public void clear() {
        errors.clear();
    }

    /**
     * Get the size of the error entries
     * @return int Returns the number of entries in this ErrorTable
     */
    public int size() {
        return errors.size();
    }

    /**
     * Get an Comparator of the entries in this ErrorTable
     * @return Iterator Returns an Enumeration of the entries in this ErrorTable
     */
    public Iterator getEntries() {
        return errors.iterator();
    }

    /**
     * Adds an Vertex/error pair entry to this ErrorTable
     * @param vertex Vertex
     * @param error double
     * @return boolean The success of the addidtion to this ErrorTable
     */
    public boolean addEntry(Vertex vertex, double error) {
        Entry entry = new Entry(vertex, error);
        return errors.add(entry);
    }

    /**
     * Remove an entry from this ErrorTable based on the Vertex identifier
     * @param vertex Vertex
     * @return boolean Returns the status of the attempted remove
     */
    public boolean removeEntry(Vertex vertex) {
        for (Iterator i = errors.iterator(); i.hasNext(); ) {
            Entry e = (Entry)i.next();
            if (e.containsVertex(vertex)) return errors.remove(vertex);
        }
        return false;
    }

    /**
     * Get the error associated with the Vertex from this ErrorTable
     * @param vertex Vertex
     * @return double The error associated with the Vertex
     */
    final public double getError(Vertex vertex) {
        double error = Double.NaN;
        for (Iterator i = errors.iterator(); i.hasNext(); ) {
            Entry entry = (Entry)i.next();
            if (entry.containsVertex(vertex)) {
                error = entry.getError();
            }
            else {
                new Exception("getError(vertex) Vertex not found");
            }
        }
        return error;
    }

    /**
     * Set the error associated with the Vertex in this ErrorTable
     * @param vertex Vertex
     * @param error double
     */
    final public void setError(Vertex vertex, double error) {
        for (Iterator i = errors.iterator(); i.hasNext(); ) {
            Entry entry = (Entry)i.next();
            if (entry.containsVertex(vertex)) {
                entry.setError(error);
            }
            else {
                new Exception("setError(vertex) Vertex not found");
            }
        }
    }
}
