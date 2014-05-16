package com.angrynerds.ai.pathfinding;

import com.angrynerds.gameobjects.map.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Basti
 * Date: 07.11.13
 * Time: 21:01
 * To change this template use File | Settings | File Templates.
 */
public class ClosestHeuristic implements AStarHeuristic{

    public float getCost(Map map, int TYPE, int x, int y, int tx, int ty) {
        float dx = tx - x;
        float dy = ty -y;

        float result = (float) Math.sqrt(dx*dx + dy*dy);

        return result ;
    }
}
