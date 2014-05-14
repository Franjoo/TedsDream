package com.angrynerds.gameobjects;

import aurelienribon.tweenengine.TweenAccessor;
import com.angrynerds.ai.pathfinding.AStarPathFinder;
import com.angrynerds.ai.pathfinding.Path;
import com.angrynerds.gameobjects.creatures.Creature;
import com.angrynerds.gameobjects.items.HealthPotion;
import com.angrynerds.gameobjects.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;

/**
 * Created with IntelliJ IDEA.
 * User: Basti
 * Date: 02.11.13
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
public class Enemy extends Creature implements Disposable{

    // pathfinding relevant
    private Path path;
    private Path oldPath;
    private AStarPathFinder pathFinder;
    private Vector2 nextStep = new Vector2();
    private int nextStepInPath = 1;
    private int ranX;
    private int ranY;
    private int xTilePosition;
    private int yTilePosition;
    private int xTilePlayer;
    private int yTilePlayer;
    private float angle;
    private Vector2 velocity = new Vector2();
    private int speed = 120;
    private float tolerance = 1.0f;

    // interaction
    private Map map;
    private Player player;

    // stats
    private float minDmg = 3.2f;
    private float maxDmg = 7.1f;
    private final float cooldown = 1.5f;

    private float health = 100f;
    private float nextAttackTime = 0;
    private float alpha = 1;

    // animation
    private AnimationState state;
    private AnimationListener animationListener;

    // sound
    private Sound sound;

    // global flags
    private boolean alive = true;


    public Enemy(String name, String path, String skin, Player player, float scale) {
        super(name, path, skin, scale);

        this.player = player;
        this.health = 100;

        minDmg = 3.2f;
        maxDmg = 7.1f;

        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/ingame/attack_0.wav"));

    }

    public Enemy(String name, String path, String skin, Player player, float scale, float ap, float hp) {
        this(name, path, skin, player,scale);

        minDmg = ap - 0.25f * ap;
        maxDmg = ap + 0.25f * ap;

        health = hp;
    }


    private void setAnimationStates() {

        AnimationStateData stateData = new AnimationStateData(skeletonData);
        stateData.setMix("move", "attack", 0.2f);
        stateData.setMix("attack", "move", 0.2f);
        stateData.setMix("attack", "die", 0.5f);
        stateData.setMix("move", "die", 0.2f);


        state = new AnimationState(stateData);
        animationListener = new AnimationListener();
        state.addListener(animationListener);
        state.addAnimation(0, "move", true, 0);
    }


    public void init(float x, float y) {
        this.x = x;
        this.y = y;

        map = Map.getInstance();
        pathFinder = AStarPathFinder.getInstance();

        updatePositions();
        path = getNewPath();
        ranX = -1 + (int) (+(Math.random() * 3));
        ranY = -1 + (int) (+(Math.random() * 3));
        setAnimationStates();

    }


    public void render(SpriteBatch batch) {
        super.render(batch);
    }


    public void update(float deltatime) {
        super.update(deltatime);
        updatePositions();
        // find new path
        if(getNewPath() != null)
          path = getNewPath();

        if (alive) {

            // update pathfinding attributes


            // enemy is far from player (move)
//
//            final float x_d = player.getX() - x;
//            final float y_d = player.getY() - y;
//            final float dist = (float) Math.sqrt(x_d * x_d + y_d * y_d);
//
//            System.out.println("dist: " + dist);

            if (path != null && path.getLength() >= 1) {
                moveToPlayer(deltatime);

                // append move animation
                if (state.getCurrent(0).getNext() == null) {
                    state.setAnimation(0, "move", false);
                    state.addAnimation(0, "move", true, 0);
                }
            }

            // enemy in front of player (attack)
            else {

                // append attack animation
                if (state.getCurrent(0).getNext() == null) {
                    state.setAnimation(0, "attack", false);
                    state.addAnimation(0, "attack", true, 0);
                }

                // attack player (animation is active)
                else if (state.getCurrent(0).getAnimation().getName().equals("attack")) {
                    attack();
                }

            }

            // if not alive and not dead - die (triggers die animation)
        } else if (state.getCurrent(0) != null
                && !state.getCurrent(0).getAnimation().getName().equals("die")) {

            state.setAnimation(0, "die", false);

            // fade enemy out when dead
        } else {
            alpha -= 0.005;
            Color c = skeleton.getColor();
            skeleton.getColor().set(c.r, c.g, c.b, alpha);

            // remove from map
            if (alpha <= 0) map.removeFromMap(this);
        }


        // update animation
        state.apply(skeleton);
        state.update(deltatime);
    }

    public void updatePositions() {

        xTilePosition = (int) Math.floor((x) / map.getTileWidth());
        yTilePosition = (int) Math.floor((y) / map.getTileHeight());
        xTilePlayer = (int) Math.floor((player.x) / map.getTileWidth());
        yTilePlayer = (int) Math.floor((player.y) / map.getTileHeight());
    }


    public Path getNewPath() {
        oldPath = path;

        if(xTilePlayer + ranY < map.getNumTilesX() && xTilePlayer >= 0 && yTilePlayer +ranY < map.getNumTilesY() && yTilePlayer >= 0) {
            if(pathFinder.findPath(1, xTilePosition, yTilePosition, xTilePlayer + ranX, yTilePlayer + ranY) != null)
         return pathFinder.findPath(1, xTilePosition, yTilePosition, xTilePlayer + ranX, yTilePlayer + ranY);
        }
        return oldPath;

    }

    public int getTilePostionX() {
        return xTilePosition;
    }

    public int getTilePostionY() {
        return yTilePosition;
    }


    public void moveToPlayer(float deltatime) {

        // todo hier steckt wird der fehler stecken, der das flackern verursacht

        skeleton.setFlipX((player.x - x >= 0));

        if (path != null && nextStepInPath < path.getLength()) {

            nextStep = new Vector2((float) path.getStep(nextStepInPath).getX() * map.getTileWidth(), (float) path.getStep(nextStepInPath).getY() * map.getTileHeight());
            angle = (float) Math.atan2(path.getStep(nextStepInPath).getY() * map.getTileHeight() - y, path.getStep(nextStepInPath).getX() * map.getTileWidth() - x);
            velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);


            if ((int) x != (int) nextStep.x) {
                x = x + velocity.x * deltatime;

            }

            if (yTilePosition != (int) nextStep.y) {
                y = (y + velocity.y * deltatime);
            }

        }
    }

    private boolean isReached(int i) {
        return Math.abs(path.getStep(i).getX() * map.getTileWidth() - getX()) <= speed / tolerance * Gdx.graphics.getDeltaTime() &&
                Math.abs(path.getStep(i).getY() * map.getTileHeight() - getY()) <= speed / tolerance * Gdx.graphics.getDeltaTime();
    }

    @Override
    public void attack() {
        nextAttackTime -= Gdx.graphics.getDeltaTime();

        if (nextAttackTime <= 0 && player.getSkeletonBounds().aabbIntersectsSkeleton(getSkeletonBounds())) {

            // calculate random damage
            float dmg = (float) (minDmg + Math.random() * (maxDmg - minDmg));
            player.setDamage(dmg);

            // refresh attack timer
            nextAttackTime = cooldown;

            // sound
            sound.play();

        }
    }


    public float getHealth() {
        return health;
    }

    private void setHealth(float health) {
        this.health = health;
        if (health <= 0){
            alive = false;
            if(Math.random() >= 0.6) map.addItem(new HealthPotion(x,y));
        }

    }

    public void setDamage(float dmg) {
        if(alive)
            setHealth(health - dmg);
    }

    @Override
    public void dispose() {

    }

    class AnimationListener implements AnimationState.AnimationStateListener {

        // todo SOUNDS!

        @Override
        public void event(int trackIndex, Event event) {
        }

        @Override
        public void complete(int trackIndex, int loopCount) {
            String completedState = state.getCurrent(trackIndex).toString();
            switch (completedState) {
                case "die":
                    System.out.println("killed enemy");
                    break;
            }
        }

        @Override
        public void start(int trackIndex) {
            String completedState = state.getCurrent(trackIndex).toString();
            switch (completedState) {
                case "die":
                    System.out.println("die animation started");
                    break;
            }
        }

        @Override
        public void end(int trackIndex) {
            String completedState = state.getCurrent(trackIndex).toString();
            switch (completedState) {
                case "die":
                    System.out.println("die animation ended");
                    break;
            }
        }
    }

}
