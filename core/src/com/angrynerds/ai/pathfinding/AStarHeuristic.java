package com.angrynerds.ai.pathfinding;

import com.angrynerds.gameobjects.map.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian
 * Date: 07.11.13
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public interface AStarHeuristic {

    public float getCost(Map map, int TYPE, int x, int y, int tx, int ty);

}
