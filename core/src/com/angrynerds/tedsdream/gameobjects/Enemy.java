package com.angrynerds.tedsdream.gameobjects;

import com.angrynerds.tedsdream.ai.pathfinding.AStarPathFinder;
import com.angrynerds.tedsdream.ai.pathfinding.Path;
import com.angrynerds.tedsdream.gameobjects.creatures.Creature;
import com.angrynerds.tedsdream.gameobjects.items.HealthPotion;
import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
public class Enemy extends Creature implements Disposable {

    // pathfinding relevant
    private Path path;
    private Path oldPath;
    private AStarPathFinder pathFinder;
    private Vector2 nextStep = new Vector2();
    private int nextStepInPath = 1;
    private int ranX;
    private int ranY;
    private int ranZ;
    private int xTilePosition;
    private int yTilePosition;
    private int zTilePosition;
    private int xTilePlayer;
    private int yTilePlayer;
    private int zTilePlayer;
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

    private float skeletonWidth;
    private float skeletonHeight;

    private float health = 100f;
    private float nextAttackTime = 0;
    private float alpha = 1;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    // animation
    private AnimationState state;
    private AnimationListener animationListener;

    private ParticleEffect bloodParticle;

    // sound
    private Sound sound;

    // global flags
    private boolean alive = true;

    public Enemy(String name, String path, String skin, float scale) {
        super(name, path, skin, scale);

        this.health = 100;

        minDmg = 3.2f;
        maxDmg = 7.1f;

        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/ingame/attack_0.wav"));
    }

    public Enemy(String name, String path, String skin, float scale, float ap, float hp) {
        this(name, path, skin, scale);

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


    public void renderPath(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1,0,0,1);
        shapeRenderer.rect(x, z, map.getProperties().tileWidth, map.getProperties().tileHeight);
        if (path != null) {
            for (int i = 0; i < path.getLength() - 1; i++) {
                float x1 = path.getStep(i).getX() * map.getProperties().tileWidth;

                float x2 = path.getStep(i + 1).getX() * map.getProperties().tileWidth;
                float z1 = path.getStep(i).getZ() * map.getProperties().tileHeight;
                float z2 = path.getStep(i + 1).getZ() * map.getProperties().tileHeight;
                System.out.println(x2 + "    " + z2);
                shapeRenderer.line(x1, z1, x2, z2);
            }

        }
        shapeRenderer.end();
    }


    public void init(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

        map = Map.getInstance();
        pathFinder = AStarPathFinder.getInstance();

//        updatePositions();
//        path = getNewPath();
//        ranX = -1 + (int) (+(Math.random() * 3));
//        ranY = -1 + (int) (+(Math.random() * 3));
        setAnimationStates();


        bloodParticle = new ParticleEffect();
        bloodParticle.load(Gdx.files.internal("particles/blood.p"), Gdx.files.internal("particles"));
        //map.getParticleEffects().add(bloodParticle);

    }

    public void render(SpriteBatch batch) {
        super.render(batch);
        batch.begin();
        bloodParticle.setPosition(x, z + this.getSkeletonBounds().getHeight() / 1.5f);
        bloodParticle.draw(batch);
        batch.end();
    }

    public void update(float deltatime) {
        super.update(deltatime);

        if (map.getPlayers() != null) {
            if(player == null)
                player = map.getPlayers().get(0);

            updatePositions();
            bloodParticle.update(deltatime);
            // find new path
            if (getNewPath() != null)
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
        }
       renderPath(shapeRenderer);
        // update animation
        state.apply(skeleton);
        state.update(deltatime);


    }

    public void updatePositions() {
        xTilePosition = (int) Math.floor((x) / map.getProperties().tileWidth);
        zTilePosition = (int) Math.floor((z) / map.getProperties().tileHeight);
        xTilePlayer = (int) Math.floor((player.x) / map.getProperties().tileWidth);
        zTilePlayer = (int) Math.floor((player.z) / map.getProperties().tileHeight);
    }


    public Path getNewPath() {
        oldPath = path;

        if (xTilePlayer  < map.getProperties().numTilesX && xTilePlayer >= 0 && zTilePlayer  <map.getProperties().numTilesZ && zTilePlayer >= 0) {
            if (pathFinder.findPath(1, xTilePosition, zTilePosition, xTilePlayer , zTilePlayer ) != null)
                return pathFinder.findPath(1, xTilePosition, zTilePosition, xTilePlayer , zTilePlayer );
        }
        return oldPath;
    }

    public int getTilePostionX() {
        return xTilePosition;
    }

    public int getTilePostionY() {
        return yTilePosition;
    }

    public int getTilePostionZ() {
        return zTilePosition;
    }

    public void moveToPlayer(float deltatime) {
        // todo hier steckt wird der fehler stecken, der das flackern verursacht

        skeleton.setFlipX((player.x - x >= 0));

        if (path != null && nextStepInPath < path.getLength()) {
            nextStep = new Vector2((float) path.getStep(nextStepInPath).getX() * map.getProperties().tileWidth, (float) path.getStep(nextStepInPath).getZ() * map.getProperties().tileHeight);
            angle = (float) Math.atan2(path.getStep(nextStepInPath).getZ() * map.getProperties().tileHeight - z, path.getStep(nextStepInPath).getX() * map.getProperties().tileWidth - x);
            velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);
            if ((int) x != (int) nextStep.x) {
                x = x + velocity.x * deltatime;
            }
            if (zTilePosition != (int) nextStep.y) {
                z = (z + velocity.y * deltatime);
            }
        }
    }

    private boolean isReached(int i) {
        return Math.abs(path.getStep(i).getX() * map.getProperties().tileWidth - getX()) <= speed / tolerance * Gdx.graphics.getDeltaTime() &&
                Math.abs(path.getStep(i).getY() * map.getProperties().tileHeight - getY()) <= speed / tolerance * Gdx.graphics.getDeltaTime();
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
        if (health <= 0) {
            alive = false;
            if (Math.random() >= 0.6) map.addItem(new HealthPotion(x, y));
        }
    }

    public float getWidth() {
        return getSkeletonBounds().getWidth();
    }

    public float getHeight() {
        return getSkeletonBounds().getHeight();
    }

    public void setDamage(float dmg) {
        if (alive)
            setHealth(health - dmg);
    }

    public ParticleEffect getBloodParticle() {
        return bloodParticle;
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
