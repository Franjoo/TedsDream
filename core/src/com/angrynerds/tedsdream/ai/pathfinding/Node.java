package com.angrynerds.tedsdream.ai.pathfinding;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian
 * Date: 07.11.13
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class Node implements Comparable {
    /** The x coordinate of the node */
    private int x;
    /** The y coordinate of the node */
    private int y;



    /** The path cost for this node */
    private float cost;



    /** The parent of this node, how we reached it in the search */
    protected Node parent;



    /** The heuristic cost of this node */
    private float heuristic;
    /** The search depth of this node */
    private int depth;


    /**
     * Create a new node
     *
     * @param x The x coordinate of the node
     * @param y The y coordinate of the node
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(float heuristic) {
        this.heuristic = heuristic;
    }

    public Node getParent() {
        return parent;
    }

    /**
     * Set the parent of this node
     *
     * @param parent The parent node which lead us to this node
     * @return The depth we have no reached in searching
     */
    public int setParent(Node parent) {
        depth = parent.depth + 1;
        this.parent = parent;

        return depth;
    }

    /**
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(Object other) {
        Node o = (Node) other;

        float f = heuristic + cost;
        float of = o.heuristic + o.cost;

        if (f < of) {
            return -1;
        } else if (f > of) {
            return 1;
        } else {
            return 0;
        }
    }


}