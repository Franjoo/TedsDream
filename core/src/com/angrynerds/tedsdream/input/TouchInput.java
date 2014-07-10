package com.angrynerds.tedsdream.input;

import com.angrynerds.tedsdream.ui.ControllerUI;
import com.angrynerds.tedsdream.ui.MyButton;
import com.angrynerds.tedsdream.util.States;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 14.11.13
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public class TouchInput extends DragListener implements IGameInputController {

    private ControllerUI controller;
    private Array<Integer> touchOrder;

    private Array<Integer> attackOrder;
    private Array<Integer> jumpOrder; //midButton -> topButton
    private Array<Integer> smashOrder; //midButton -> topButton -> botButton
    private Array<Integer> turnAroundOrder; // top -> right -> bot -> left
    private Array<Integer> dashOrderRight;
    private Array<Integer> dashOrderLeft;

    States.Animation state;

    public TouchInput(ControllerUI controller) {
        this.controller = controller;
        touchOrder = new Array<Integer>();


        init();
    }

    private void init() {
        state = States.Animation.IDLE;

        jumpOrder = new Array<>(new Integer[]{controller.getMidButton().getId(), controller.getTopButton().getId()});
        smashOrder = new Array<>(new Integer[]{controller.getMidButton().getId(), controller.getTopButton().getId(), controller.getBotButton().getId()});
        turnAroundOrder = new Array<>(new Integer[]{controller.getTopButton().getId(), controller.getRightButton().getId(), controller.getBotButton().getId(), controller.getLeftButton().getId()});
        attackOrder = new Array<>(new Integer[]{controller.getMidButton().getId()});
        dashOrderRight = new Array<>(new Integer[]{controller.getLeftButton().getId(), controller.getMidButton().getId(), controller.getRightButton().getId()});
        dashOrderLeft = new Array<>(new Integer[]{controller.getRightButton().getId(), controller.getMidButton().getId(), controller.getLeftButton().getId()});
    }


    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer,
                        int button) {
        super.touchUp(event, x, y, pointer, button);
        System.out.println("touchup drag");
        for (MyButton but : controller.getButtons()) {
            if ((but != null) && but.getOver())
                but.setOver(false);
            but.setChecked(false);
            MyButton.overCounter = 0;
        }
        if (touchOrder.equals(attackOrder)) {
            state = States.Animation.ATTACK;
        } else if (touchOrder.equals(jumpOrder)) {
            System.out.println("jump to the sky");
            state = States.Animation.JUMP;
        } else if (touchOrder.equals(smashOrder)) {
            System.out.println("smaaaaaaash");
            state = States.Animation.SMASH;
        } else if (touchOrder.equals(turnAroundOrder))
            System.out.println("turn around");

        else if (touchOrder.equals(dashOrderRight)) {
            state = States.Animation.DASH_RIGHT;
        } else if (touchOrder.equals(dashOrderLeft)) {
            state = States.Animation.DASH_LEFT;
        }
        //state = State.IDLE;
        touchOrder.clear();
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer,
                      Actor fromActor) {

        System.out.println(event.getTarget());


        MyButton b;
        b = (MyButton) event.getTarget();
        if (!b.getOver()) {
            b.setOver(true);
            touchOrder.add(b.getId());
            b.setChecked(true);
        }
        super.enter(event, x, y, pointer, fromActor);
    }

    @Override
    public float get_stickX() {
        System.out.println(controller.getTouchpad().getKnobPercentX());
        return controller.getTouchpad().getKnobPercentX();
    }

    @Override
    public float get_stickY() {
        return controller.getTouchpad().getKnobPercentY();
    }

    @Override
    public States.Animation getState() {
        return state;
    }

    @Override
    public boolean get_isA() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean get_isB() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setState(States.Animation state) {
        this.state = state;

    }
}
