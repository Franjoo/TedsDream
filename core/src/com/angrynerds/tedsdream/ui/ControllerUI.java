package com.angrynerds.tedsdream.ui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;
import com.angrynerds.tedsdream.input.TouchInput;
import com.angrynerds.tedsdream.tweens.LifebarAccessor;
import com.angrynerds.tedsdream.util.C;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * User: Franjo
 * Date: 07.11.13
 * Time: 16:05
 * Project: GameDemo
 */
public class ControllerUI {

    private TouchInput listener;

    private Touchpad touchpad;
    private MyButton midButton;
    private MyButton topButton;
    private MyButton botButton;
    private MyButton leftButton;
    private MyButton rightButton;
    private Table buttonTable;
    private MyButton[] buttons = new MyButton[5];
    private Lifebar lifeBar;

    private Stage stage;

    private TweenManager manager;
    private boolean showInputUI = true;

    public ControllerUI() {
        init();
    }

    private void init() {

        //stage = new Stage(800, 480, true, PlayScreen.getBatch());
          stage =new Stage();
        lifeBar = new Lifebar("lifebar2_03.png", "lifebar2b_03.png", "lifebar2c_03.png");
        lifeBar.setPosition(10, C.VIEWPORT_HEIGHT - lifeBar.getHeight());

        Skin skin = new Skin();
        initSkin(skin);
        initTouchPad(skin);
        initButtons(skin);
        initButtonTable();
        addListenerToButtons();
        fillButtonArray();

        Gdx.input.setInputProcessor(stage);

        addUIElementsToStage();

        tweenLifebar();
    }

    private void tweenLifebar() {
        Tween.registerAccessor(Lifebar.class, new LifebarAccessor());
        manager = new TweenManager();
        Tween.to(lifeBar, LifebarAccessor.SCALE_XY, 3)
                .target(100, 200)
                .ease(Bounce.OUT)
                .repeatYoyo(10, .5f)
                .start(manager);
    }

    private void addUIElementsToStage() {
        stage.addActor(touchpad);
        stage.addActor(buttonTable);
//        stage.addActor(topButton);
//        stage.addActor(rightButton);
//        stage.addActor(botButton);
//        stage.addActor(leftButton);
//        stage.addActor(midButton);
        stage.addActor(lifeBar);
    }

    private void fillButtonArray() {
        buttons[0] = midButton;
        buttons[1] = topButton;
        buttons[2] = rightButton;
        buttons[3] = botButton;
        buttons[4] = leftButton;
    }

    private void addListenerToButtons() {
        listener = new TouchInput(this);
        topButton.addListener(listener);
        midButton.addListener(listener);
        rightButton.addListener(listener);
        botButton.addListener(listener);
        leftButton.addListener(listener);
    }

    private void initButtonTable() {
        buttonTable = new Table();
        buttonTable.setBounds(0, 0, leftButton.getWidth() + midButton.getWidth() + rightButton.getWidth(), topButton.getHeight() + midButton.getHeight() + botButton.getHeight());
        buttonTable.setPosition(stage.getWidth() - buttonTable.getWidth(), 0);
//        buttonTable.add().width(leftButton.getWidth()).height(topButton.getHeight());
//        buttonTable.add(topButton).width(topButton.getWidth()).height(topButton.getHeight()).bottom();
//        buttonTable.add().width(rightButton.getWidth()).height(topButton.getHeight()).row();
//        buttonTable.add(leftButton).width(leftButton.getWidth()).height(leftButton.getHeight());
//        buttonTable.add(midButton).width(midButton.getWidth()).height(midButton.getHeight());
//        buttonTable.add(rightButton).width(rightButton.getWidth()).height(rightButton.getHeight()).row();
//        buttonTable.add().width(leftButton.getWidth()).height(botButton.getHeight());
//        buttonTable.add(botButton).width(botButton.getWidth()).height(botButton.getHeight()).top();
//        buttonTable.add().width(rightButton.getWidth()).height(botButton.getHeight());
        buttonTable.add(topButton).colspan(3).expand().row();
        buttonTable.add(leftButton).expand();
        buttonTable.add(midButton).expand();
        buttonTable.add(rightButton).expand().row();
        buttonTable.add(botButton).colspan(3).expand();
    }

    private void initButtons(Skin skin) {
        midButton = new MyButton(0, skin.getDrawable("mid_up"), skin.getDrawable("mid_down"));
        //midButton.setBounds(C.VIEWPORT_WIDTH - midButton.getWidth(), midButton.getHeight()/3, 100, 100);

        topButton = new MyButton(1, skin.getDrawable("top_up"), skin.getDrawable("top_down"));
        //topButton.setBounds(midButton.getX(), midButton.getY()+midButton.getHeight()/1.25f, 100, 70);

        rightButton = new MyButton(2, skin.getDrawable("right_up"), skin.getDrawable("right_down"));
        //rightButton.setBounds(midButton.getX()+midButton.getWidth()/1.25f, midButton.getY()-midButton.getHeight()/15, 70, 100);

        botButton = new MyButton(3, skin.getDrawable("bottom_up"), skin.getDrawable("bottom_down"));
        //botButton.setBounds(midButton.getX(), midButton.getY()-midButton.getHeight()/1.8f, 100, 70);

        leftButton = new MyButton(4, skin.getDrawable("left_up"), skin.getDrawable("left_down"));
        //leftButton.setBounds(midButton.getX()-midButton.getWidth()/1.75f, midButton.getY()-midButton.getHeight()/15, 70, 100);
    }

    private void initTouchPad(Skin skin) {
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        Drawable joystickBG = skin.getDrawable("joystick_bg");
        Drawable joystickKnob = skin.getDrawable("joystick_knob");
        style.background = joystickBG;
        style.knob = joystickKnob;
        touchpad = new Touchpad(10, style);
        touchpad.setBounds(15, 75, 120, 120);
    }

    private void initSkin(Skin skin) {
        skin.add("joystick_bg", new Texture("ui/ingame/controls/joystick_bg.png"));
        skin.add("joystick_knob", new Texture("ui/ingame/controls/joystick_knob.png"));
        skin.add("mid_up", new Texture("ui/ingame/controls/mid_up.png"));
        skin.add("mid_down", new Texture("ui/ingame/controls/mid_down.png"));
        skin.add("top_up", new Texture("ui/ingame/controls/top_up.png"));
        skin.add("top_down", new Texture("ui/ingame/controls/top_down.png"));
        skin.add("right_up", new Texture("ui/ingame/controls/right_up.png"));
        skin.add("right_down", new Texture("ui/ingame/controls/right_down.png"));
        skin.add("left_up", new Texture("ui/ingame/controls/left_up.png"));
        skin.add("left_down", new Texture("ui/ingame/controls/left_down.png"));
        skin.add("bottom_up", new Texture("ui/ingame/controls/bottom_up.png"));
        skin.add("bottom_down", new Texture("ui/ingame/controls/bottom_down.png"));
    }

    public void update(float delta) {
        stage.act(delta);
        manager.update(delta);
    }

    // public void render() {
//        stage.draw();
    // }

    // getters
    public MyButton[] getButtons() {
        return buttons;
    }

    public Touchpad getTouchpad() {
        return touchpad;
    }

    public MyButton getMidButton() {
        return midButton;
    }

    public MyButton getTopButton() {
        return topButton;
    }

    public MyButton getBotButton() {
        return botButton;
    }

    public MyButton getLeftButton() {
        return leftButton;
    }

    public MyButton getRightButton() {
        return rightButton;
    }

    public TouchInput getListener() {
        return listener;
    }

    public Lifebar getLifeBar() {
        return lifeBar;
    }

    public void render(float delta) {
            stage.draw();
//        Tween.to(lifeBar, -1 , 1.0f)
//        .target(-1, -1)
//        .ease(Bounce.INOUT)
//        .delay(1.0f)
//        .repeatYoyo(2, 0.5f)
//        .start(manager);
    }

    public void hideInputUI() {
        stage.getActors().removeValue(touchpad,true);
        stage.getActors().removeValue(buttonTable,true);
    }

    public void show() {
        stage.addActor(touchpad);
        stage.addActor(buttonTable);

        Gdx.input.setInputProcessor(stage);
    }
}
