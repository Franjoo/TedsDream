package com.angrynerds.tedsdream.ai.pathfinding;

import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.angrynerds.tedsdream.renderer.CollisionHandler;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian
 * Date: 07.11.13
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
public class AStarPathFinder {

    private static String TAG = AStarPathFinder.class.getSimpleName();

    private static AStarPathFinder instance = null;

    private final Map map;
    private Path path;
    private AStarHeuristic heuristic;
    private int maxSearchDistance;
    private boolean allowDiagMovement;
    private Node[][] nodes;
    private ArrayList closedList = new ArrayList();
    private SortedList openList = new SortedList();
    private CollisionHandler collisionHandler;


    private AStarPathFinder(Map map, int maxSearchDistance,
                            boolean allowDiagMovement, AStarHeuristic heuristic) {
        this.heuristic = heuristic;
        this.map = map;
        this.maxSearchDistance = maxSearchDistance;
        this.allowDiagMovement = allowDiagMovement;
        this.collisionHandler = new CollisionHandler(map);

        nodes = new Node[map.getProperties().numTilesX][map.getProperties().numTilesZ];
        // System.out.println(map.getMapWidth());
        for (int x = 0; x < map.getProperties().numTilesX; x++) {
            for (int z = 0; z < map.getProperties().numTilesZ; z++) {
                nodes[x][z] = new Node(x, z);
            }
        }
        path = new Path();
        instance = this;
    }

    public Path findPath(int enemieType, int sx, int sz, int tx, int tz) {
        path.flushPath();

        if (collisionHandler.isSolid(tx * map.getProperties().tileWidth, tz * map.getProperties().tileHeight)) {
            return null;

        }
        if (tx < 0 || tz < 0)
            return null;

        /** Init A Star */
        nodes[sx][sz].setDepth(0);
        nodes[sx][sz].setCost(0);
        closedList.clear();
        openList.clear();
        openList.add(nodes[sx][sz]);

        nodes[tx][tz].parent = null;

        int currentDepth = 0;

        while (currentDepth <= maxSearchDistance && openList.getSize() != 0) {
            Node currentNode = getFirstInOpen();
            if (currentNode == nodes[tx][tz])
                break;

            removeFromOpen(currentNode);
            addToClosed(currentNode);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (!(x == 0 && z == 0)) {
                        int xp = x + currentNode.getX();
                        int zp = z + currentNode.getZ();

                        if (isValidLocation(1, sx, sz, xp, zp)) {

                            float nextStepCost = currentNode.getCost() + getMovementCost();
                            Node neighbour = nodes[xp][zp];
//                            map.pathFinderVisited(xp, yp);

                            if (nextStepCost < neighbour.getCost()) {
                                if (inOpenList(neighbour)) {
                                    removeFromOpen(neighbour);
                                }
                                if (inClosedList(neighbour)) {
                                    removeFromClosed(neighbour);
                                }
                            }

                            if (!inOpenList(neighbour) && !(inClosedList(neighbour))) {
                                neighbour.setCost(nextStepCost);
                                neighbour.setHeuristic(getHeuristicCost(1, xp, zp, tx, tz));
                                currentDepth = Math.max(currentDepth, neighbour.setParent(currentNode));
                                addToOpen(neighbour);
                            }
                        }
                    }
                }
            }

        }

        if (nodes[tx][tz].parent == null)
            return null;


        Node target = nodes[tx][tz];
        while (target != nodes[sx][sz]) {
            path.prependStep(target.getX(), target.getZ());
            target = target.parent;
        }
        path.prependStep(sx, sz);
        //  for(int f = 0; f<path.getLength(); f++)
        // System.out.println(path.getStep(f).getX() + "   "  + path.getStep(f).getY());
        return path;
    }


    protected boolean isValidLocation(int mover, int sx, int sz, int x, int z) {
        boolean invalid = ((x < 0) || (z < 0)) || (x >= map.getProperties().numTilesX || (z >= map.getProperties().numTilesZ));

        if ((!invalid) && ((sx != x) || (sz != z))) {
            invalid = collisionHandler.isSolid(x * map.getProperties().tileWidth, z * map.getProperties().tileHeight);
        }

        return !invalid;
    }


    private boolean inOpenList(Object o) {


        return openList.contains(o);
    }

    private float getHeuristicCost(int TYPE, int xp, int zp, int tx, int tz) {
        return heuristic.getCost(map, TYPE, xp, zp, tx, tz);
    }

    boolean inClosedList(Object o) {

        return closedList.contains(o);
    }

    private void removeFromClosed(Object o) {

        closedList.remove(o);
    }


    private Node getFirstInOpen() {
        return (Node) openList.getFirst();
    }

    private void removeFromOpen(Node node) {
        openList.remove(node);
    }

    private void addToClosed(Node node) {

        closedList.add(node);
    }

    private void addToOpen(Node node) {

        openList.add(node);
    }

    private int getMovementCost() {
        //  return new Random().nextInt(2);
        return 1;
    }

    //*** SINGLETON ***//

    public static AStarPathFinder getInstance() {
        if (instance == null) throw new InstantiationError(TAG + " has not been initialized");
        return instance;
    }

    public static void initialize(Map map, int maxSearchDistance,
                                  boolean allowDiagMovement, AStarHeuristic heuristic) {
        new AStarPathFinder(map, maxSearchDistance, allowDiagMovement, heuristic);
    }

}
