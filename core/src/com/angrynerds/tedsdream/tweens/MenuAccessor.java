package com.angrynerds.tedsdream.tweens;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 19.12.13
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
public class MenuAccessor implements TweenAccessor<Button> {

    // tweening types
    public static final int GLOWING = 5;
    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int SCALE_XY = 4;

    @Override
    public int getValues(Button button, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X: returnValues[0] = button.getX(); return 1;
            case POSITION_Y: returnValues[0] = button.getY(); return 1;
            case POSITION_XY:
                returnValues[0] = button.getX();
                returnValues[1] = button.getY();
                return 2;
            case SCALE_XY:
                returnValues[0] = button.getX();
                returnValues[1] = button.getY();
                return 2;
            case GLOWING:
                //    returnValues[0] = lifebar.getLife().getTexture();
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Button button, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X: button.setX(newValues[0]); break;
            case POSITION_Y: button.setY(newValues[0]); break;
            case POSITION_XY:
                button.setX(newValues[0]);
                button.setY(newValues[1]);
                break;
            case SCALE_XY:
                button.setScaleX(newValues[0]);
                button.setScaleY(newValues[1]);
                break;
            default: assert false; break;
        }
    }
}
