package com.angrynerds.tedsdream.gameobjects;

import com.angrynerds.tedsdream.collision.Detector;
import com.angrynerds.tedsdream.events.UpdatePlayerEvent;
import com.angrynerds.tedsdream.gameobjects.items.HealthPotion;
import com.angrynerds.tedsdream.gameobjects.items.Item;
import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.angrynerds.tedsdream.input.IGameInputController;
import com.angrynerds.tedsdream.renderer.CollisionHandler;
import com.angrynerds.tedsdream.screens.GameController;
import com.angrynerds.tedsdream.util.C;
import com.angrynerds.tedsdream.util.States;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;

/**
 * Author: Franz Benthin
 */
public class Player extends Creature {

    // player constants
    public static final float HP_MAX = 300;
    public static final float AP_MAX = 30;

    // constants
    private final float epsilon = C.EPSILON;

    // map
//    private Camera camera;
    private Map map;
    private CollisionHandler collisionHandler;

    // movement
    private float z;
    private float velocityX;
    private float velocityY;
    private float velocityX_MAX = 320;
    private float velocityY_MAX = 220;
    private  float shadowHeight;
    private float shadowWidth;

    // stats
    private float maxHP = 100;
    private float actHP;
    private float atckDmg = 4;

    // helper attributes
    private Vector2 vec2 = new Vector2();
    private boolean flipped;
    private Vector2 _pt = new Vector2();

    // animation
    private AnimationState state;
    private AnimationListener animationListener;
    private Array<String> attackAnimations;

    // sound_sword
    private Sound sound_sword;
    private Sound sound_dash;

    private boolean dashRight;
    private boolean alive = true;

    // detector
    private Detector detector;

    // input
    private IGameInputController input;
    private GameController game;
    public int attackFlag = 0;

    boolean upleft, downleft, upright, downright;

    // multiplayer
    private UpdatePlayerEvent updateEvent;

    private int id = -1;

    public Player(IGameInputController input, GameController game) {
        super(new TextureAtlas(Gdx.files.internal("spine/ted/ted.atlas")), "spine/ted/", 0.20f,AP_MAX,HP_MAX);
        this.input = input;
        this.game = game;

        init();
    }

    public void init() {
//        map = Map.getInstance();
        detector.initialize(map.getTiledMap());
        detector = Detector.getInstance();
        collisionHandler = new CollisionHandler(map);

        x = 500;
//        y = 150;
        y = 150;
        actHP = maxHP;

        setAnimationStates();

        //*** sounds
        sound_sword = Gdx.audio.newSound(Gdx.files.internal("sounds/ingame/lightsaber.mp3"));
        sound_dash = Gdx.audio.newSound(Gdx.files.internal("sounds/ingame/dash.wav"));

        setCurrentState();

        updateEvent = new UpdatePlayerEvent();
    }

    private void setAnimationStates() {
        attackAnimations = new Array<>();
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        for (int i = 0; i < stateData.getSkeletonData().getAnimations().size; i++) {
            String from = stateData.getSkeletonData().getAnimations().get(i).getName();
            if (from.startsWith("attack")) attackAnimations.add(from);
            for (int j = 0; j < stateData.getSkeletonData().getAnimations().size; j++) {
                String to = stateData.getSkeletonData().getAnimations().get(i).getName();

                if (!from.equals(to)) stateData.setMix(from, to, 0.4f);
            }
        }
//
//        stateData.setMix("move", "dash", 0.4f);
//        stateData.setMix("move", "attack_1", 0.4f);
//
//        stateData.setMix("attack_1", "move", 0.4f);
//        stateData.setMix("dash", "move", 0.4f);
//        stateData.setMix("move", "dash", 0.4f);
//        stateData.setMix("move", "die", 0.4f);
//        stateData.setMix("attack_1", "die", 0.4f);
//        stateData.setMix("jump", "die", 0.4f);
//        stateData.setMix("dash", "die", 0.4f);

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        animationListener = new AnimationListener();
        state.addListener(animationListener);
        state.setAnimation(0, "move", true);
    }



    public void draw(SpriteBatch batch){
        super.draw(batch);
    }

    public String getAnimation() {
        return state.getCurrent(0).toString();
    }

    public void attack() {
        for (Creature e : game.getEnemies()) {
            if (e.getSkeletonBounds().aabbIntersectsSkeleton(getSkeletonBounds())) {
//                AddBloobParticlesForRender(e.getBloodParticle(), e.getX(), e.getY());
                e.setDamage(atckDmg);
                System.out.println("atccking enemy " + e.getHP());
            }
        }
        sound_sword.play();
        //actHP -= 50;
    }

    private void AddBloobParticlesForRender(ParticleEffect particle, float x, float y) {
//        ParticleEffect particle = new ParticleEffect();
//        particle.load(Gdx.files.internal("particles/blueblood.p"), Gdx.files.internal("particles"));
//        particle.setPosition(x, y);
        particle.start();
    }

    public void remoteUpdate(float delta){
        super.update(delta);
        setCurrentState();

        state.update(delta);
        state.apply(skeleton);
    }

    public void update(float deltaTime) {
        super.update(deltaTime);
        shadowHeight = getSkeletonBounds().getHeight();
        shadowWidth = getSkeletonBounds().getWidth();
        if(alive){
            // set v in x and y direction
            velocityX = input.get_stickX() * deltaTime * velocityX_MAX;
            velocityY = input.get_stickY() * deltaTime * velocityY_MAX;
            if (velocityX != 0 && velocityY != 0 && input.getState() == States.Animation.IDLE)
                input.setState(States.Animation.RUN);

            if (alive) {

//            if (!(input instanceof RemoteInput)) {
                // set v in x and y direction
                velocityX = input.get_stickX() * deltaTime * velocityX_MAX;
                velocityY = input.get_stickY() * deltaTime * velocityY_MAX;
                if (velocityX != 0 && velocityY != 0 && input.getState() == States.Animation.IDLE)
                    input.setState(States.Animation.RUN);

                if (velocityX == 0 && velocityY == 0 && input.getState() == States.Animation.RUN)
                    input.setState(States.Animation.IDLE);
            }


            if (velocityX == 0)
                skeleton.setFlipX(flipped);
            else
                skeleton.setFlipX(velocityX < 0);

            setCurrentState();


            checkForNextToItem();

            letPlayerDontRunOut();
            // apply and update skeleton
            //        Animation animation = state.getCurrent(0).getAnimation();
            //        if(animation.getName().equals("run_test")){
            //            System.out.println(velocityX);
            //            animation.apply(skeleton,skeleton.getTime(),skeleton.getTime() * input.get_stickX(),true,null);
            //        }

            // apply and update skeleton
//        Animation animation = state.getCurrent(0).getAnimation();
//        if(animation.getName().equals("move")){
//            System.out.println(velocityX);
//            animation.apply(skeleton,skeleton.getTime(),skeleton.getTime() * input.get_stickX(),true,null);
//        }
            // was flipped for velocityX == 0 in next update
            flipped = skeleton.getFlipX();
//        }

        state.update(deltaTime);



        state.apply(skeleton);

        updateEvent.set(id,x,y,input.getState(),skeleton.getFlipX());
    } }

    private void letPlayerDontRunOut() {
        if (y >= map.getProperties().tileHeight * 6)
            y = map.getProperties().tileHeight * 6;
        if (y <= 0)
            y = 0;
        if (x <= 20)
            x = 20;
        if (x >= map.getProperties().mapWidth - 20)
            x = map.getProperties().mapWidth - 20;
    }

    public float getHeight() {
        return shadowHeight;
    }

    public float getWidth(){
        return shadowWidth;
    }
    public float getX(){
        return  x ;
    }


    public float getY(){
        return  y ;
    }

    private void updatePositionAttributes(float deltaTime, Vector2 collisionPosition) {
        if (state.getCurrent(0).toString().equals("dash")) {
            x += dash(deltaTime);
        } else {
            x = collisionPosition.x;
        }
        y = collisionPosition.y;
    }

    private void checkForNextToItem() {
        int tolerance = 30;
        float playerPositionX = x + this.getSkeletonBounds().getWidth() / 2;
        float playerPositionY = y + this.getSkeletonBounds().getHeight() / 2;
        for (Item item : game.getItems()) {
            float itemPositionX = item.getX() + item.region.getTexture().getWidth() / 2;
            float itemPositionY = item.getY() + item.region.getTexture().getHeight() / 2;
            if (itemPositionX > playerPositionX - tolerance && itemPositionX < playerPositionX + tolerance) {
                if (itemPositionY > playerPositionY - tolerance && itemPositionY < playerPositionY + tolerance) {
                    collectItem(item);
                }
            }
        }
    }

    private void collectItem(Item item) {
        if (item instanceof HealthPotion)
            setActualHP(actHP + 8);
        game.getItems().removeValue(item, true);
    }

    private float dash(float deltaTime) {
        if (dashRight) {
            skeleton.setFlipX(false);
            return deltaTime * Gdx.graphics.getWidth() / 2;
        } else {
            skeleton.setFlipX(true);
            return -deltaTime * Gdx.graphics.getWidth() / 2;
        }
    }

    private void setCurrentState() {

//        System.out.println("set state: " + input.getState());

//        if (input != null) {

        String current = state.getCurrent(0).toString();

        if (current.equals("move") || current.equals("idle")) {
            if (input.getState() == States.Animation.JUMP && !current.equals("jump")) {
                state.setAnimation(0, "jump", false);
                state.addAnimation(0, "idle", true, 0);
                //            state.addAnimation(1, "move", true, jumpAnimation.getDuration() - 30);
                //            state.addAnimation(1, "move", false, 0);
            }
            if (input.getState() == States.Animation.ATTACK && !current.startsWith("attack_1")) {
                attack();
                String attack = attackAnimations.get((int) (Math.random() * attackAnimations.size));
                state.setAnimation(0, attack, false);
                state.addAnimation(0, "idle", true, 0);
            }
            if ((input.getState() == States.Animation.DASH_RIGHT || input.getState() == States.Animation.DASH_LEFT) && !current.equals("dash")) {
                if (input.getState() == States.Animation.DASH_RIGHT)
                    dashRight = true;
                else
                    dashRight = false;
                state.setAnimation(0, "dash", false);
                state.addAnimation(0, "idle", true, 0);
                sound_dash.play();
            }
            if ((input.getState() == States.Animation.DEAD) && !current.equals("die")) {
                state.setAnimation(0, "die", false);
            }
        }
        if (input.getState() == States.Animation.IDLE && !current.equals("idle")) {
            if (current.equals("move"))
                state.setAnimation(0, "idle", false);
            state.addAnimation(0, "idle", true, 0);
        }
        if (input.getState() == States.Animation.RUN && current.equals("idle")) {
            state.setAnimation(0, "move", false);
            state.addAnimation(0, "move", true, 0);
        }
        if (input.getState() != States.Animation.DEAD && input.getState() != States.Animation.RUN)
            input.setState(States.Animation.IDLE);
    }

    public void setCurrentState(States.Animation state) {
        input.setState(state);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public void getMyCorners(float pX, float pY) {
        // calculate corner coordinates
        int downY = (int) Math.floor(map.getProperties().mapHeight - (pY) / map.getProperties().tileHeight);
        int upY = (int) Math.floor(map.getProperties().mapHeight - (pY + map.getProperties().mapHeight) / map.getProperties().tileHeight);
        int leftX = (int) Math.floor((pX) / map.getProperties().tileWidth);
        int rightX = (int) Math.floor((pX + map.getProperties().mapWidth) / map.getProperties().tileWidth);

        // check if the corner is a wall
        checkForWall(downY, upY, leftX, rightX);
    }

    private void checkForWall(int downY, int upY, int leftX, int rightX) {
        upleft = collisionHandler.isSolid(leftX, upY);
        downleft = collisionHandler.isSolid(leftX, downY);
        upright = collisionHandler.isSolid(rightX, upY);
        downright = collisionHandler.isSolid(rightX, downY);
    }


    public float getMaxHP() {
        return maxHP;
    }

    public float getActualHP() {
        return actHP;
    }

    public void setActualHP(float hp) {
        if (actHP > 0) {
            if (hp > maxHP)
                actHP = maxHP;
            else actHP = hp;
        }
        if (actHP <= 0 && alive)
            die();
    }

    private void die() {
        alive = false;
        state.setAnimation(0, "die", false);
//        input.setState(State.DEAD);
//        setCurrentState();
    }

    public void setMaxHP(int hp) {
        maxHP = hp;
    }

    public void setDamage(float dmg) {
        setActualHP(actHP - dmg);
    }


    public IGameInputController getInput() {
        return input;
    }

    public States.Animation getState() {
        return input.getState();
    }

    public void setState(States.Animation state) {
        input.setState(state);
    }

    public void setFlip(boolean flip) {
        skeleton.setFlipX(flip);
    }


    class AnimationListener implements AnimationState.AnimationStateListener {
        @Override
        public void event(int trackIndex, Event event) {
//            System.out.println(trackIndex + " event: " + state.getCurrent(trackIndex) + ", " + event.getData().getName());
        }

        @Override
        public void complete(int trackIndex, int loopCount) {
//            System.out.println(trackIndex + " complete: " + state.getCurrent(trackIndex) + ", " + loopCount);
//            System.out.println(state.getCurrent(trackIndex));
            if (state.getCurrent(trackIndex).toString().equals("jump")) {
                state.setAnimation(0, "move", true);
            }
        }

        @Override
        public void start(int trackIndex) {
//            System.out.println(trackIndex + " start: " + state.getCurrent(trackIndex));
        }

        @Override
        public void end(int trackIndex) {
//            System.out.println(trackIndex + " end: " + state.getCurrent(trackIndex));
        }

    }

    public int getID() {
        return id;
    }

    public void setID(final int id) {
        this.id = id;
    }

    public UpdatePlayerEvent getUpdateEvent() {
        return updateEvent;
    }
}
