package com.angrynerds.ui;

import com.angrynerds.input.IGameInputController;
import com.angrynerds.util.C;
import com.angrynerds.util.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * User: Franjo
 * Date: 26.10.13
 * Time: 13:12
 * Project: Main
 */
public class DeprecatedControllUI implements IGameInputController {

    private  Sprite sprite;
    // stage
    private Stage stage;

    // touchPad
    private Touchpad touchpad;
    private TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;

    private Camera camera;

    // buttons
    private Button btn_A;
    private Button btn_B;

    public DeprecatedControllUI(Camera camera) {
      //  stage = new Stage(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT, true);
       // stage.setCamera(camera);

        this.camera = camera;
        Gdx.input.setInputProcessor(stage);

        sprite = new Sprite();



        init();
    }


    private void init() {
        //*** Touchpad  ***//
        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", new Texture("data/touchBackground.png"));
        touchpadSkin.add("touchKnob", new Texture("data/touchKnob.png"));

        touchpadStyle = new TouchpadStyle();
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(15, 15, 200, 200);
        touchpad.setPosition(-C.VIEWPORT_WIDTH / 2, -C.VIEWPORT_HEIGHT / 2);

        stage.addActor(touchpad);

        //*** Buttons ***//
        Skin btn_A_skin = new Skin();
        Skin btn_B_skin = new Skin();

        Texture texture_A = new Texture("data/buttons/Xbox360_Button_A.png");
        Texture texture_B = new Texture("data/buttons/Xbox360_Button_B.png");

        texture_A.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texture_B.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        btn_A_skin.add("btn_A", texture_A);
        btn_B_skin.add("btn_B", texture_B);

        Drawable dA = btn_A_skin.getDrawable("btn_A");
        Drawable dB = btn_B_skin.getDrawable("btn_B");

        Button.ButtonStyle style_A = new Button.ButtonStyle(dA, dA, dA);
        Button.ButtonStyle style_B = new Button.ButtonStyle(dB, dB, dB);

        btn_A = new Button(style_A);
        btn_B = new Button(style_B);

        btn_A.setSize(btn_A.getWidth() * 1.4f, btn_A.getHeight() * 1.4f);
        btn_B.setSize(btn_B.getWidth() * 1.4f, btn_B.getHeight() * 1.4f);
//        btn_B.scale(5);

        btn_B.setPosition(C.VIEWPORT_WIDTH / 2 - btn_B.getWidth() - 20, -C.VIEWPORT_HEIGHT / 2);
        btn_A.setPosition(C.VIEWPORT_WIDTH / 2 - 1.3f * btn_B.getWidth() - btn_A.getWidth(), -C.VIEWPORT_HEIGHT / 2);

        stage.addActor(btn_A);
        stage.addActor(btn_B);

    }

    public void render(SpriteBatch batch) {
        System.out.println("render");

//        stage.getSpriteBatch().setProjectionMatrix(camera.combined);

        stage.act();
        stage.draw();

//        stage.getSpriteBatch().begin();
//        sprite.draw(batch);
//        stage.getSpriteBatch().end();




    }


    //<editor-fold desc="IGameInputController methods">
    @Override
    public float get_stickX() {
        return touchpad.getKnobPercentX();
    }

    @Override
    public float get_stickY() {
        return touchpad.getKnobPercentY();
    }

    @Override
    public State getState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean get_isA() {
        return btn_A.isPressed();
    }

    @Override
    public boolean get_isB() {
        return btn_B.isPressed();
    }

    @Override
    public void setState(State state) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    //</editor-fold>
}
