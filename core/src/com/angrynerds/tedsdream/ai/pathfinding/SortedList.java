package com.angrynerds.tedsdream.ai.pathfinding;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian
 * Date: 07.11.13
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
public class SortedList{

    private ArrayList list = new ArrayList();

    public SortedList(){


    }

    public int getSize(){

        return list.size();
    }

    public Object getFirst(){

        return list.get(0);
    }

    public Object getLast(){

        return list.get(list.size()-1);
    }

    public void clear(){

        list.clear();
    }

    public void add(Object o){
        list.add(o);
        Collections.sort(list);


    }

    public void remove(Object o){
        list.remove(o);

    }

    public boolean contains(Object o) {
        return list.contains(o);
    }

    public Object getByIndex(int i){
        return list.get(i);

    }
}
