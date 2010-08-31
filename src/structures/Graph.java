package structures;

import java.util.*;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: Graph object for storing the clustering concept.</p>
 * A graph stores Vertex objects connected by Edge objects.
 * A graph can not have multiple edges connecting two vertices.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (e-mail K.A.J.Doherty@herts.ac.uk)
 * @version 1.0 06 July 2004 First release
 * @version 1.1 09 July 2004 Added javadoc, comments, DFS and reworked toString()
 * @version 1.2 21 September 2004 Now extends Observable
 * @version 1.3 06 October 2004 Implemented Sub-Graph functionality
 * @version 1.4 21 October 2004 Pretty toString with v and e() text
 * @version 2.0 15 February 2005 No longer an Observable
 * @version 2.1 22 February 2005 Reinstated Observer with notifyObservers() added to all add and remove methods
 * @version 2.2 Removed compiler > 1.5 container unchecked warnings
 */
public class Graph extends Observable implements Cloneable {
    /** The container Object for this Graph's Edges */
    protected Vector<Edge> edges_ = new Vector<Edge>();

    /** The container Object for this Graph's Vertices */
    protected Vector<Vertex> verts_ = new Vector<Vertex>();


    // The list of paths
    protected LinkedList<LinkedList> distance_pathq = new LinkedList<LinkedList>();

    // begin with a single path going nowhere
    protected LinkedList<Vertex> distance_path = new LinkedList<Vertex>();


    /** No parameter constructor */
    public Graph() {
        super();
    }

    /**
     * Get an Iterator of all the vertices in this Graph
     * @return an iterator to all the vertices
     */
    public Iterator getAllVertices() {
        return verts_.iterator();
    }

    public Vertex [] getVerticesArray() {
      return (Vertex [])verts_.toArray();
    }

    /**
     * Get an Iterator of all the edges in this Graph
     * @return an iterator to all the edges
     */
    synchronized public Iterator getAllEdges() {
        return edges_.iterator();
    }

    /**
     * Does this graph contain the specified vertex
     * @param vertex Vertex
     * @return boolean Returns true if the graph contains the vertex, false otherwise
     */
    synchronized final public boolean hasVertex(final Vertex vertex) {
        return verts_.contains(vertex);
    }

    /**
     * Get the degree (number of incident edges) of this vertx
     * @param vertex the vertex to get the degree of
     * @return the degree of this vertex
     */
    synchronized public int degree(final Vertex vertex) {
        int degree = 0;
        // is vertex a part of this graph?
        if (isValid(vertex)) {
            for (Iterator i = this.getAllEdges(); i.hasNext(); ) {
                // get all the edges of the graph
                final Edge edge = (Edge)i.next();
                // and if the edge contains the vertex, increment the degree
                if (isValid(edge)) {
                    if (edge.contains(vertex)) {
                        degree++;
                    }
                }
            }
        }
        else {
            System.err.print("Graph.degree() called\n");
            System.err.print("Graph is\n" + this.toString());
            NoSuchElementException e = new NoSuchElementException("Graph doesn't contain Vertex " + vertex);
            e.printStackTrace();
            throw e;
        }
        return degree;
    }

    /**
     * Add an edge to this graph
     * @param edge the edge to insert into the graph
     * @return boolean status
     */
    synchronized public boolean addEdge(final Edge edge) {
        final boolean status = edges_.add(edge);
        setChanged();
        notifyObservers();
        notifyAll();
        return status;
    }

    /**
     * Add a vertex to this graph
     * @param vertex the vertex to insert into this graph
     * @return boolean status
     */
    synchronized public boolean addVertex(final Vertex vertex) {
        final boolean status = verts_.add(vertex);
        setChanged();
        notifyObservers();
        notifyAll();
        return status;
    }

    /**
     * Remove a vertex from this graph
     * @param vertex the vertex to remove from this graph
     * @return status
     */
    synchronized public boolean deleteVertex(final Vertex vertex) {
        if (isValid(vertex)) {
            final boolean status = verts_.remove(vertex);
            setChanged();
            notifyObservers();
            notifyAll();
            return status;
        } else {
            System.err.print("Graph.deleteVertex(Vertex " + vertex.toString() + ") called\n");
            System.err.print("Graph is\n" + this.toString());
            NoSuchElementException e = new NoSuchElementException("Graph.deleteVertex() called with an invalid vertex");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Remove an edge from this graph
     * @param edge the edge to remove from this graph
     * @return status
     */
    synchronized public boolean deleteEdge(final Edge edge) {
        if (isValid(edge)) {
            final boolean status = edges_.remove(edge);
            setChanged();
            notifyObservers();
            notifyAll();
            return status;
        } else {
            System.err.print("Graph.deleteEdge(Edge " + edge.toString() + ") called\n");
            System.err.print("Graph is\n" + this.toString());
            NoSuchElementException e = new NoSuchElementException("Graph.deleteEdge() called with an invalid edge:\n" +
                edge.toString());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get the edges that are incident to a vertex
     * @param vertex the vertex from which the incident edges are taken
     * @return an Iterator of edges
     */
    synchronized public Iterator getIncidentEdges(final Vertex vertex) {
        Vector<Edge> edgesFound = new Vector<Edge>();
        // is vertex a part of this graph?
        if (isValid(vertex)) {
            for (Iterator i = edges_.iterator(); i.hasNext(); ) {
                // get all the edges in this graph
                final Edge edge = (Edge)i.next();
                // and if the edge contains the vertex, it is incident
                if (edge.contains(vertex)) {
                    edgesFound.add(edge);
                }
            }
        }
        else {
            NoSuchElementException e = new NoSuchElementException("Graph.getIncidentEdges(" + vertex.toString() +
                ") Vertex not found\n");
            e.printStackTrace();
            throw e;
        }
        // return an iterator over the edges
        return edgesFound.iterator();
    }

    /**
     * Determine the number of disjoint Graphs in this Graph
     * @return int Return the number of sub-Graphs
     */
    synchronized final public int numSubGraphs() {
        int i = 0;
        for (Iterator iter = getSubGraphs(); iter.hasNext(); ) {
            iter.next();
            i++;
        }
        return i;
    }

    /**
     * Determine whether graph A vertices is a Union of the graph B vertices.
     * @param a Graph
     * @param b Graph
     * @return boolean Returns true if the Union of Graph a and Graph
     * b is not empty, else returns false. Note the double iteration as there is something not quite right with this method
     */
    synchronized final static public boolean isUnion(final Graph a, final Graph b) {
        for (Iterator ai = a.getAllVertices(); ai.hasNext(); ) {
            final Vertex va = (Vertex)ai.next();
            for (Iterator bi = b.getAllVertices(); bi.hasNext(); ) {
                if (va.equals((Vertex)bi.next())) return true;
            }
        }
        for (Iterator bi = b.getAllVertices(); bi.hasNext(); ) {
            final Vertex vb = (Vertex)bi.next();
            for (Iterator ai = a.getAllVertices(); ai.hasNext(); ) {
                if (vb.equals((Vertex)ai.next())) return true;
            }
        }
        return false;
    }

    /**
     * Determine the disjointedness of the graph structures
     * @return Iterator An Iterator over the sub-Graphs in this Graph
     */
    synchronized public Iterator getSubGraphs() {
        // subGraphs is a Vector of Graphs
        Vector<Graph> subGraphs = new Vector<Graph>();
        for (Iterator vertices = this.getAllVertices(); vertices.hasNext(); ) {
            // from this vertex
            final Vertex v = (Vertex)vertices.next();
            // Does it exist in a subgraph?
            boolean found = false;
            for (Iterator sgs = subGraphs.iterator(); sgs.hasNext(); ) {
                final Graph sg = (Graph)sgs.next();
                if (sg.isValid(v)) {
                    found = true;
                }
            }
            // If the vertex is not found then
            if (!found) {
                // Create a new subGraph
                final Graph newGraph = new Graph();
                subGraphs.add(newGraph);
                // Add the Vertex
                newGraph.addVertex(v);
                // Add all the connected vertices
                for (Iterator connected = getConnectedComponents(v); connected.hasNext(); ) {
                    final Vertex vc = (Vertex)connected.next();
                    if (!newGraph.isValid(vc)) {
                        newGraph.addVertex(vc);
                    }
                }
            }
        }
        // Now generate the edges in the sub-graphs
        for (Iterator sgs = subGraphs.iterator(); sgs.hasNext(); ) {
            final Graph sg = (Graph)sgs.next();
            for (Iterator vertices = sg.getAllVertices(); vertices.hasNext(); ) {
                // from this vertex
                final Vertex v = (Vertex)vertices.next();
                for (Iterator connected = getConnectedComponents(v); connected.hasNext(); ) {
                    final Vertex vc = (Vertex)connected.next();
                    // and the edges that connect these vertices
                    if (this.areConnected(v, vc)) {
                        // Only add the connection if it it doesn't exist in sg
                        if (!sg.areConnected(v, vc)) sg.addEdge(this.connection(v, vc));
                    }
                }
            }
        }
        return subGraphs.iterator();
    }

    /**
     * Get the neighbouring vertices to a given vertex
     * @param vertex the vertex for which to gather the neighbouring vertices
     * @return an Iterator of vertices
     */
    synchronized public Iterator getNeighbours(final Vertex vertex) {
        Vector<Vertex> neighboursFound = new Vector<Vertex>();
        // is vertex a part of this graph?
        if (isValid(vertex)) {
            for (Iterator i = getIncidentEdges(vertex); i.hasNext(); ) {
                // get all the incident edges
                // and add the vertex at the other end
                neighboursFound.add(((Edge)i.next()).otherEnd(vertex));
            }
        }
        else {
            NoSuchElementException e = new NoSuchElementException(
                                          "Graph.getNeighbours(" +
                                          vertex.toString() +
                                          ") Vertex not found\n");
                                          e.printStackTrace();
            throw e;
        }
        // return an iterator over the vertices
        return neighboursFound.iterator();
    }

    /**
     * Get the edge connecting the two vertices
     * @param v1 one end of the edge
     * @param v2 the other end of the edge
     * @return the edge connecting the two vertices, null if there is no edge
     */
    synchronized public Edge connection(final Vertex v1, final Vertex v2) {
        Edge connection = null;
        Vector<Edge> found = new Vector<Edge>();
        // ignore connection to self
        if (v1 != v2) {
            // are both the vertices in the graph?
            if ((isValid(v1)) && (isValid(v2))) {
                // get all the edges
                for (Iterator i = edges_.iterator(); i.hasNext(); ) {
                    final Edge edge = (Edge)i.next();
                    // and if the edge contains both vertices, assume it is a connection!
                    if ((edge.contains(v1)) && (edge.contains(v2))) {
                        found.add(edge);
                    }
                }
            } else {
                System.err.print("Graph.connection(" + v1 + ", " + v2 + ")\n");
                System.err.print("Graph.connection() failed validity checks of two vertices\n");
                System.err.print("Graph.connection() Graph is\n" + this.toString() + "\n");
                System.err.print("Graph.connection() Sub-Graphs are\n");
                for (Iterator i = this.getSubGraphs(); i.hasNext(); ) {
                    Graph g = (Graph)i.next();
                    System.err.print("...Sub-Graph" + g.toString() + "\n");
                }
                System.err.print("Graph.connection() Vertex Container is: " + verts_.toString() + "\n");
                System.err.print("Graph.connection() Edge Container is: " + edges_.toString() + "\n");
                RuntimeException e = new RuntimeException("areConnected");
                e.printStackTrace();
                throw e;
            }
            if (found.size() > 1) {
                RuntimeException e = new RuntimeException("areConnected(" + v1.toString() + ", " + v2.toString() +
                    " by more than one connection\n");
                e.printStackTrace();
                throw e;
            } else if (found.size() == 1) {
                connection = (Edge)found.firstElement();
            } else {
                return null;
            }
        }
        return connection;
    }

    /**
     * Tests if the two vertices are connected by a single edge
     * @param v1 one end of the edge
     * @param v2 the other end of the edge
     * @return true if the edges are connected, false otherwise
     */
    synchronized public boolean areConnected(final Vertex v1, final Vertex v2) {
        return (connection(v1, v2) != null);
    }

    /**
     * The number of edges in the graph
     * @return an int of the number of edges
     */
    synchronized public int numEdges() {
        return edges_.size();
    }

    /**
     * The number of vertices in the graph
     * @return an int of the number of vertices
     */
    synchronized public int numVertices() {
        return verts_.size();
    }

    /**
     * toString() overrides the Object protected method
     * @return a String describing the Graph by sub-Graph grouping
     */
    public String toString() {
        StringBuffer s = new StringBuffer(256);
        for (Iterator graphs = getSubGraphs(); graphs.hasNext(); ) {
            final Graph graph = (Graph)graphs.next();
            s.append("Vertices: ");
            for (Iterator i = graph.verts_.iterator(); i.hasNext(); ) {
                s.append(((Vertex)i.next()).toString());
                if (i.hasNext()) s.append(",");
            }
            s.append(" Edges: ");
            for (Iterator i = graph.edges_.iterator(); i.hasNext(); ) {
                s.append(((Edge)i.next()).toString());
                if (i.hasNext()) {
                    s.append(",");
                } else {
                    s.append(";");
                }
            }
        }
        return s.toString();
    }

    // private - no javadoc
    private boolean isValid(final Object o) {
        boolean valid = false;
        if (o instanceof Edge) {
            valid = edges_.contains(o);
        } else if (o instanceof Vertex) {
            valid = verts_.contains(o);
        } else {
            RuntimeException e = new RuntimeException("Graph.contains(Object) called with unrecognised class" +
                o.getClass().toString() + "\n");
            e.printStackTrace();
            throw e;
        }
        return valid;
    }

    /**
     * Equals() overrides the Object protected method and tests this Graph's equality with the obj parameter
     * @param obj Object The Object to compare against this graph
     * @return boolean Returns true if the Graphs are equal, false otherwise
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Graph) {
            Graph candidate = (Graph)obj;
            return (candidate.verts_.containsAll(this.verts_)) && (this.verts_.containsAll(candidate.verts_)) &&
                (candidate.edges_.containsAll(this.edges_)) && (this.edges_.containsAll(candidate.edges_));
        } else {
            return false;
        }
    }


    /**
     * Calculate the distance in the graph in L1
     * @param start Vertex
     * @param goal Vertex
     * @return integer distance
     */
    public int distance(final Vertex start, final Vertex goal) {

      // Cleaer the list of paths
      distance_pathq.clear();

      // and begin with a single path going nowhere
      distance_path.clear();
      distance_path.add(start);
      // and store in the list of paths
      distance_pathq.add(distance_path);

      // Then recurse bfs on the pathq
      if ( bfs(goal, distance_pathq) ) {
        return ((LinkedList) distance_pathq.getFirst()).size() - 1;
      } else {
        return 0;  // no path between vertices;
      }
    }



    final private boolean foundGoal(Vertex goal, LinkedList<LinkedList> pathq) {
      LinkedList path = (LinkedList) pathq.getFirst();
      Vertex head = (Vertex) path.getFirst();
      if ( head == goal ) {
        return true;
      } else {
        return false;
      }
    }


    final private boolean bfs(Vertex goal, LinkedList<LinkedList> pathq) {

      // if pathq is empty - failed
      while ( !pathq.isEmpty() ) {

        if (foundGoal(goal, pathq)) return true;

        LinkedList path = (LinkedList) pathq.removeFirst();

        Iterator i = this.getNeighbours( (Vertex) path.getFirst() );
        while (i.hasNext()) {
            Vertex child = (Vertex) i.next();
            LinkedList<Vertex> newpath = new LinkedList<Vertex>();
            for (ListIterator members = path.listIterator(); members.hasNext(); ){
              newpath.add( (Vertex) members.next() );
            }
            // Don't backtrack
            if ( !newpath.contains(child) )  {
              newpath.addFirst(child);
              if ( !pathq.contains(newpath) ){
                pathq.addLast(newpath);
              }
            } else {
              newpath = null;
            }
        }
      }
      return false;
    }


    /**
     * Perform a depth first search on the graph, begining at a specified vertex
     * @param vertex The vertex from where to begin the search.
     * @return An Iterator of connected vertices
     *
     * Use a depth first search dependent on the set/get visisted state
     * of the vertices
     */
    synchronized public Iterator getConnectedComponents(final Vertex vertex) {
        Vector<Vertex> visited = new Vector<Vertex>();
        resetVisitState();
        DFS(vertex);
        for (Iterator i = getAllVertices(); i.hasNext(); ) {
            Vertex v = (Vertex)i.next();
            if (v.getVisited()) {
                visited.add(v);
            }
        }
        return visited.iterator();
    }

    // private - no javadoc
    private void DFS(Vertex vertex) {
        vertex.setVisited(true);
        for (Iterator neighbours = getNeighbours(vertex); neighbours.hasNext(); ) {
            Vertex nextNeighbour = (Vertex)neighbours.next();
            if (!nextNeighbour.getVisited()) {
                DFS(nextNeighbour);
            }
        }
    }

    // private - no javadoc
    private void resetVisitState() {
        for (Iterator i = getAllVertices(); i.hasNext(); ) {
            ((Vertex)i.next()).setVisited(false);
        }
    }
}
