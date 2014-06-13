package com.angrynerds.tedsdream.tweens;

import aurelienribon.tweenengine.TweenAccessor;
import com.angrynerds.tedsdream.ui.Lifebar;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 19.12.13
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
public class LifebarAccessor implements TweenAccessor<Lifebar> {

    // tweening types
    public static final int GLOWING = 5;
    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int SCALE_XY = 4;

    @Override
    public int getValues(Lifebar lifebar, int tweenType, float[] returnValues) {
        switch (tweenType){
            case POSITION_X: returnValues[0] = lifebar.getX(); return 1;
            case POSITION_Y: returnValues[0] = lifebar.getY(); return 1;
            case POSITION_XY:
                returnValues[0] = lifebar.getX();
                returnValues[1] = lifebar.getY();
                return 2;
            case SCALE_XY:
                returnValues[0] = lifebar.getX();
                returnValues[1] = lifebar.getY();
                return 2;
            case GLOWING:
            //    returnValues[0] = lifebar.getLife().getTexture();
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Lifebar lifebar, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X: lifebar.setX(newValues[0]); break;
            case POSITION_Y: lifebar.setY(newValues[0]); break;
            case POSITION_XY:
                lifebar.setX(newValues[0]);
                lifebar.setY(newValues[1]);
                break;
            case SCALE_XY:
                lifebar.setScaleX(newValues[0]);
                lifebar.setScaleY(newValues[1]);
                break;
            default: assert false; break;
        }
    }
}
