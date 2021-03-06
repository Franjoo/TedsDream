package com.angrynerds.tedsdream.gameobjects;

import com.angrynerds.tedsdream.collision.Detector;
import com.angrynerds.tedsdream.gameobjects.creatures.Creature;
import com.angrynerds.tedsdream.gameobjects.items.HealthPotion;
import com.angrynerds.tedsdream.gameobjects.items.Item;
import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.angrynerds.tedsdream.input.IGameInputController;
import com.angrynerds.tedsdream.renderer.CollisionHandler;
import com.angrynerds.tedsdream.screens.game.UpdatePlayerEvent;
import com.angrynerds.tedsdream.util.C;
import com.angrynerds.tedsdream.util.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.*;

/**
 * class that represents the Player
 */
public class Player extends Creature {

    // constants
    private final float epsilon = C.EPSILON;

    // map
//    private Camera camera;
    private Map map;
    private CollisionHandler collisionHandler;

    // movement
   // private float z;
    private float velocityX;
    private float velocityY;
    private float velocityZ;
    private float velocityX_MAX = 320;
    private float velocityY_MAX = 220;
    private float velocityZ_MAX = 220;

    private  float shadowHeight;
    private float shadowWidth;

    // stats
    private float maxHP = 100;
    private float actHP;
    private float atckDmg = 25;

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
    public int attackFlag = 0;

    boolean upleft, downleft, upright, downright;

    // multiplayer
    private UpdatePlayerEvent updateEvent;

    private int id = -1;

    public Player(IGameInputController input, Map map) {
        super("ted", "spine/ted/", null, 0.20f);
        this.input = input;
        this.map = map;
//        walkAnimation = skeletonData.findAnimation("run_test");
//        jumpAnimation = skeletonData.findAnimation("jump");
//        showBounds = true;

        init();
    }

    public void init() {
//        map = Map.getInstance();
        detector.initialize(map.getTiledMap());
        detector = Detector.getInstance();
        collisionHandler = new CollisionHandler(map);

        x = 500;
//        y = 150;
        z = 150;
        actHP = maxHP;
        width = 32;
        height = 32;

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

    public void render(SpriteBatch batch) {
        super.render(batch);
    }

    public String getAnimation() {
        return state.getCurrent(0).toString();
    }

    @Override
    public void attack() {
        for (Enemy e : map.getEnemies()) {
            if (e.getSkeletonBounds().aabbIntersectsSkeleton(getSkeletonBounds())) {
                AddBloobParticlesForRender(e.getBloodParticle(), e.getX(), e.getY());
                e.setDamage(atckDmg);
                System.out.println("atccking enemy " + e.getHealth());
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
            velocityZ = input.get_stickY() * deltaTime * velocityZ_MAX;
            if (velocityX != 0 && velocityZ != 0 && input.getState() == State.IDLE)
                input.setState(State.RUN);

            if (alive) {

//            if (!(input instanceof RemoteInput)) {
                // set v in x and y direction
                velocityX = input.get_stickX() * deltaTime * velocityX_MAX;
                velocityZ= input.get_stickY() * deltaTime * velocityZ_MAX;
                if (velocityX != 0 && velocityZ!= 0 && input.getState() == State.IDLE) {
                    input.setState(State.RUN);
                }
                if (velocityX == 0 && velocityZ == 0 && input.getState() == State.RUN)
                    input.setState(State.IDLE);
            }

            Vector2 collisionPosition = getCollisionPosition();

            if (velocityX == 0)
                skeleton.setFlipX(flipped);
            else
                skeleton.setFlipX(velocityX < 0);

            setCurrentState();

            updatePositionAttributes(deltaTime, collisionPosition);

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

        updateEvent.set(id,x,z,input.getState(),skeleton.getFlipX());
    } }

    private void letPlayerDontRunOut() {
        if (z >= map.getProperties().tileHeight * 6)
            z = map.getProperties().tileHeight * 6;
        if (z <= 0)
            z = 0;
        if (x <= 20)
            x = 20;
        if (x >= map.getProperties().mapWidth - 20)
            x = map.getProperties().mapWidth - 20;
    }

    @Override
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
    public float getZ(){
        return  z ;
    }

    private void updatePositionAttributes(float deltaTime, Vector2 collisionPosition) {
        System.out.println(z);
        if (state.getCurrent(0).toString().equals("dash")) {
            x += dash(deltaTime);
        } else {
            x = collisionPosition.x;
        }
        z = collisionPosition.y;
    }

    private void checkForNextToItem() {
        int tolerance = 30;
        float playerPositionX = x + this.getSkeletonBounds().getWidth() / 2;
        float playerPositionZ = z + this.getSkeletonBounds().getHeight() / 2;
        for (Item item : map.getItems()) {
            float itemPositionX = item.getX() + item.region.getTexture().getWidth() / 2;
            float itemPositionZ = item.getZ() + item.region.getTexture().getHeight() / 2;
            if (itemPositionX > playerPositionX - tolerance && itemPositionX < playerPositionX + tolerance) {
                if (itemPositionZ > playerPositionZ - tolerance && itemPositionZ < playerPositionZ+ tolerance) {
                    collectItem(item);
                }
            }
        }
    }

    private void collectItem(Item item) {
        if (item instanceof HealthPotion)
            setActualHP(actHP + 8);
        map.getItems().removeValue(item, true);
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
            if (input.getState() == State.JUMP && !current.equals("jump")) {
                state.setAnimation(0, "jump", false);
                state.addAnimation(0, "idle", true, 0);
                //            state.addAnimation(1, "move", true, jumpAnimation.getDuration() - 30);
                //            state.addAnimation(1, "move", false, 0);
            }
            if (input.getState() == State.ATTACK && !current.startsWith("attack_1")) {
                attack();
                String attack = attackAnimations.get((int) (Math.random() * attackAnimations.size));
                state.setAnimation(0, attack, false);
                state.addAnimation(0, "idle", true, 0);
            }
            if ((input.getState() == State.DASH_RIGHT || input.getState() == State.DASH_LEFT) && !current.equals("dash")) {
                if (input.getState() == State.DASH_RIGHT)
                    dashRight = true;
                else
                    dashRight = false;
                state.setAnimation(0, "dash", false);
                state.addAnimation(0, "idle", true, 0);
                sound_dash.play();
            }
            if ((input.getState() == State.DEAD) && !current.equals("die")) {
                state.setAnimation(0, "die", false);
            }
        }
        if (input.getState() == State.IDLE && !current.equals("idle")) {
            if (current.equals("move"))
                state.setAnimation(0, "idle", false);
            state.addAnimation(0, "idle", true, 0);
        }
        if (input.getState() == State.RUN && current.equals("idle")) {
            state.setAnimation(0, "move", false);
            state.addAnimation(0, "move", true, 0);
        }
        if (input.getState() != State.DEAD && input.getState() != State.RUN)
            input.setState(State.IDLE);
    }

    public void setCurrentState(int state) {
        input.setState(state);
    }

    public void setPosition(float x,float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * detects whether the player collides with a solid
     * and sets his position depending on the solids
     * position and dimension
     */
    private Vector2 getCollisionPosition() {
        float nX;
        float nY;
        float nZ;

        _pt.set(getTileCollisionPosition(x, z, velocityX, velocityZ));
        nX = _pt.x;
        nZ = _pt.y;
        vec2.set(nX, nZ);

        return vec2;
    }

    private void setTileCollisionPosition() {
        //LEFT
        if (velocityX < 0) {
            if (detector.isSolid(x, y) || detector.isSolid(x, y + height)) {
                velocityX = 0;
            }

            // RIGHT
        } else if (velocityX > 0) {
            if (detector.isSolid(x + width, y) || detector.isSolid(x + width, z + height)) {
                velocityX = 0;
            }
        }

        // BOTTOM
        if (velocityY < 0) {
            if (detector.isSolid(x, y) || detector.isSolid(x + width, y)) {
                velocityY = 0;
            }
        }

        // TOP
        if (velocityY > 0) {
            if (detector.isSolid(x, y + height) || detector.isSolid(x + width, y + height)) {
                velocityY = 0;
            }
        }
    }

    private Vector2 getObjectCollisionPosition(float pX, float pY, float vX, float vY) {
        float _x = pX + vX;
        float _y = pY + vY;

        Array<Rectangle> rectangles;

        // left
        if (vX < 0) {
            rectangles = collisionHandler.getCollisionObjects(pX + vX, pY, pX + vX, pY + height);
            if (rectangles.size != 0) {
                _x = map.getXmax(rectangles) + 0.001f;
                System.out.println("collision");
            }
        }

        // right
        else if (vX > 0) {
            rectangles = collisionHandler.getCollisionObjects(pX + vX + width, pY, pX + vX + width, pY + height);
            if (rectangles.size != 0) {
                _x = map.getXmin(rectangles) - width - 0.001f;
                System.out.println("collision");
            }
        }

        // top
        if (vY > 0) {
            rectangles = collisionHandler.getCollisionObjects(pX, pY + height + vY, pX + width, pY + height + vY);
            System.out.println("solid: " + detector.isSolid(x, y));
            if (rectangles.size != 0) {
                System.out.println("top");
                _y = map.getYmin(rectangles) - height - 0.001f;
            }
        }

        // bottom
        else if (vY < 0) {
            rectangles = collisionHandler.getCollisionObjects(pX, pY + vY, pX + width, pY + vY);
            if (rectangles.size != 0) {
                _y = map.getYmax(rectangles) + 0.001f;
                System.out.println("collision");
            }
        }
        vec2.set(_x, _y);
        return vec2;
    }

    public void getMyCorners(float pX, float pZ) {
        // calculate corner coordinates
        int downZ = (int) Math.floor(map.getProperties().mapHeight - (pZ) / map.getProperties().tileHeight);
        int upZ = (int) Math.floor(map.getProperties().mapHeight - (pZ + map.getProperties().mapHeight) / map.getProperties().tileHeight);
        int leftX = (int) Math.floor((pX) / map.getProperties().tileWidth);
        int rightX = (int) Math.floor((pX + map.getProperties().mapWidth) / map.getProperties().tileWidth);

        // check if the corner is a wall
        checkForWall(downZ, upZ, leftX, rightX);
    }

    private void checkForWall(int downY, int upY, int leftX, int rightX) {
        upleft = collisionHandler.isSolid(leftX, upY);
        downleft = collisionHandler.isSolid(leftX, downY);
        upright = collisionHandler.isSolid(rightX, upY);
        downright = collisionHandler.isSolid(rightX, downY);
    }

    private Vector2 getTileCollisionPosition(float pX, float pZ, float vX, float vZ) {
        float _x = pX;
        float _z = pZ;

        float qX = pX + vX;
        float qZ = pZ + vZ;

        /* COLLIDED TILES */
        getMyCorners(qX, qZ);
        /* X-AXIS */
        if (vX < 0) {
            // botton left
            if (downleft && upleft) {
                _x = ((int) (pX) / map.getProperties().tileWidth) * map.getProperties().tileWidth;
            }
            // top left
            else if (collisionHandler.isSolid(qX, pZ + height)) {
                _x = ((int) (pX) / map.getProperties().tileWidth) * map.getProperties().tileWidth;
            } else {
                _x = qX;
            }
        } else if (vX > 0)
            // bottom right
            if (collisionHandler.isSolid(qX + width, pZ)) {
                _x = ((int) (qX) / map.getProperties().tileWidth) * map.getProperties().tileWidth - epsilon;
            }
            // top right
            else if (collisionHandler.isSolid(qX + width, pZ + height)) {
                _x = ((int) (qX) / map.getProperties().tileWidth) * map.getProperties().tileWidth - epsilon;
            } else {
                _x = qX;
            }

        /* Y_AXIS */
        if (vZ < 0) {
            // bottom left
            if (collisionHandler.isSolid(pX, qZ)) {
                _z = ((int) (pZ) / map.getProperties().tileHeight) * map.getProperties().tileHeight;
            }
            // bottom right
            else if (collisionHandler.isSolid(pX + width, qZ)) {
                _z = ((int) (pZ) / map.getProperties().tileHeight) * map.getProperties().tileHeight;
            } else {
                _z = qZ;
            }
        } else if (vZ > 0) {
            // top left
            if (collisionHandler.isSolid(pX, qZ + height)) {
                _z = ((int) (qZ) / map.getProperties().tileHeight) * map.getProperties().tileHeight - epsilon;
            }
            // top right
            else if (collisionHandler.isSolid(pX + width, qZ + height)) {
                _z = ((int) (qZ) / map.getProperties().tileHeight) * map.getProperties().tileHeight - epsilon;
            } else {
                _z = qZ;
            }
        }
        vec2.set(_x, _z);
        return vec2;
    }

    private Vector2 getMapCollisionPosition(float pX, float pY, float vX, float vY) {
        // helper variables
        float qX = pX + vX;
        float qY = pY + vY;

       /* MAP COLLISION */
        if (qX < map.getProperties().borderWidth)
            qX = map.getProperties().borderWidth;
        else if (qX + width > map.getProperties().mapWidth - map.getProperties().borderWidth)
            qX =  map.getProperties().mapWidth - map.getProperties().borderWidth - width;
        if (qY > map.getProperties().mapHeight - map.getProperties().borderWidth - 64)// map.getOffsetX() * map.getProperties().tileHeight)
            qY = map.getProperties().mapHeight - map.getProperties().borderWidth - 64;//map.getOffsetX() * map.getProperties().tileHeight;
        else if (qY < map.getProperties().borderWidth + 64)
            qY = map.getProperties().borderWidth + 64;

        vec2.set(qX, qY);
        return vec2;
    }

    private void drawRectangularShape() {
        // draw rectangular shape
        Pixmap p = new Pixmap((int) (width), (int) (height), Pixmap.Format.RGBA8888);
        Texture t = new Texture(p.getWidth(), p.getHeight(), Pixmap.Format.RGBA8888);

        p.setColor(0, 0, 0, 1);
        p.fillRectangle(0, 0, (int) width, (int) height);

        t.draw(p, 0, 0);
    }

//    public boolean isHit(float x, float y){
////        return
//    }

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

    public int getState() {
        return input.getState();
    }

    public void setState(int state) {
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
