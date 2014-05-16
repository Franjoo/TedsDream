package com.angrynerds.ai.pathfinding;

import com.angrynerds.gameobjects.map.Map;

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


    private AStarPathFinder(Map map, int maxSearchDistance,
                            boolean allowDiagMovement, AStarHeuristic heuristic) {
        this.heuristic = heuristic;
        this.map = map;
        this.maxSearchDistance = maxSearchDistance;
        this.allowDiagMovement = allowDiagMovement;

        nodes = new Node[map.getNumTilesX()][map.getNumTilesY()];
        // System.out.println(map.getMapWidth());
        for (int x = 0; x < map.getNumTilesX(); x++) {
            for (int y = 0; y < map.getNumTilesY(); y++) {
                nodes[x][y] = new Node(x, y);
            }
        }
        path = new Path();
        instance = this;
    }

    public Path findPath(int enemieType, int sx, int sy, int tx, int ty) {

        // return null;


        path.flushPath();

//        for (int i = 0; i < map.getEnemies().size ; i++) {
//              if(tx == map.getEnemies().get(i).getTilePostionX() && ty == map.getEnemies().get(i).getTilePostionY())
//                  return null;
//
//        }
//        if(map.blockedByEnemie(sx,sy)) {
//            System.out.println("Blocked");
//            return null;
//        }
        if (map.isSolid(tx * map.getTileWidth(), ty * map.getTileHeight())) {

            return null;

        }
        if (tx < 0 || ty < 0)
            return null;

        /** Init A Star */
        nodes[sx][sy].setDepth(0);
        nodes[sx][sy].setCost(0);
        closedList.clear();
        openList.clear();
        openList.add(nodes[sx][sy]);

        nodes[tx][ty].parent = null;


        int currentDepth = 0;


        while (currentDepth <= maxSearchDistance && openList.getSize() != 0) {

            Node currentNode = getFirstInOpen();
            if (currentNode == nodes[tx][ty])
                break;

            removeFromOpen(currentNode);
            addToClosed(currentNode);
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (!(x == 0 && y == 0)) {
                        int xp = x + currentNode.getX();
                        int yp = y + currentNode.getY();


                        if (isValidLocation(1, sx, sy, xp, yp)) {

                            float nextStepCost = currentNode.getCost() + getMovementCost();
                            Node neighbour = nodes[xp][yp];
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
                                neighbour.setHeuristic(getHeuristicCost(1, xp, yp, tx, ty));
                                currentDepth = Math.max(currentDepth, neighbour.setParent(currentNode));
                                addToOpen(neighbour);

                            }


                        }


                    }


                }
            }

        }

        if (nodes[tx][ty].parent == null)
            return null;


        Node target = nodes[tx][ty];
        while (target != nodes[sx][sy]) {
            path.prependStep(target.getX(), target.getY());
            target = target.parent;
        }
        path.prependStep(sx, sy);
        //  for(int f = 0; f<path.getLength(); f++)
        // System.out.println(path.getStep(f).getX() + "   "  + path.getStep(f).getY());
        return path;
    }


    protected boolean isValidLocation(int mover, int sx, int sy, int x, int y) {
        boolean invalid = ((x < 0) || (y < 0)) || (x >= map.getNumTilesX() || (y >= map.getNumTilesY()));

        if ((!invalid) && ((sx != x) || (sy != y))) {
            invalid = map.isSolid(x * map.getTileWidth(), y * map.getTileHeight());


        }

        return !invalid;

        //return true;
    }


    private boolean inOpenList(Object o) {


        return openList.contains(o);
    }

    private float getHeuristicCost(int TYPE, int xp, int yp, int tx, int ty) {
        return heuristic.getCost(map, TYPE, xp, yp, tx, ty);
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
