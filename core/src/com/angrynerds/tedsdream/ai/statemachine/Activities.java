package com.angrynerds.tedsdream.ai.statemachine;

import com.angrynerds.tedsdream.gameobjects.Creature;
import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.angrynerds.tedsdream.gameobjects.Player;
import com.angrynerds.tedsdream.screens.GameController;
import com.badlogic.gdx.math.Vector2;

/**
 * Author: Franz Benthin
 */
public class Activities {

    private static GameController game;
    private static Vector2 vec2; // helper vector

    public static void create(GameController game) {
        Activities.game = game;
    }

    private Activities() {
    }

    // static helper methods
    private static float Distance(GameObject s1, GameObject s2) {
        return Vector2.len(s2.getX() - s1.getX(), s2.getY() - s1.getY());
    }

    private static Vector2 Direction(GameObject from, GameObject to) {
        return vec2.set(to.getX() - from.getX(), to.getY() - from.getY()).nor();
    }

    private static void TriggerAnimation(Creature actor, String name) {
        if (!(actor.getCurrentAnimationName().equals(name))) {
            actor.setAnimation(name, true);
        }
    }

    // static activity classes
    public static class RunToObject extends Activity<Creature> {

        GameObject target;

        public RunToObject(Creature actor, GameObject target) {
            super(actor, Activities.game);

            this.target = target;
        }

        @Override
        public boolean update(float delta) {
            actor.moveInDirection(Direction(actor,target),actor.getSpeed(Creature.Move.RUN));
            return (actor.getX() == target.getX() && actor.getY() == target.getY());
        }

        @Override
        public float getScore() {
            return 0;
        }
    }

    public static class RunToPlayer extends Activity<Creature> {

        public RunToPlayer(Creature actor) {
            super(actor, Activities.game);
            vec2 = new Vector2(actor.getX(), actor.getY());
        }

        @Override
        public boolean update(float delta) {
            Creature creature = actor;

            TriggerAnimation(creature, "move");

            if (game.getPlayers().size > 0) {
                Player player = game.getPlayers().first();

                final float dx = player.getX() - creature.getX();
                final float dy = player.getY() - creature.getY();
                vec2.set(dx, dy);

                float distance = vec2.len();
                if (distance > 30) {
                    vec2.nor();
                    creature.moveInDirection(vec2, creature.getSpeed(Creature.Move.WALK));
                    return false;
                }

            }

            creature.moveInDirection(0, 0, 0);
            return true;
        }

        @Override
        public float getScore() {
            return 10;
        }
    }

    public static class HideFromPlayer extends EnemyActivity {

        private float hpHideValue;
        private float playerLastX, playerLastY;
        private float directionX, directionY;
        private float sameRouteTimeMax = 4f;
        private float time;

        public HideFromPlayer(Creature actor, float hpHideValue) {
            super(actor, Activities.game);

            this.hpHideValue = hpHideValue;

            vec2.set(20, 5).nor();
            directionX = vec2.x;
            directionY = vec2.y;
        }

        @Override
        public boolean update(float delta) {
            time += delta;

            Creature player = game.getPlayers().first();
            if (Distance(actor, player) <= 400) {

                TriggerAnimation(actor, "move");

                if (time > sameRouteTimeMax) {
                    time = 0;
                    Vector2 direction = getHideDirection();
                    directionX = direction.x;
                    directionY = direction.y;
                }

                actor.moveInDirection(directionX, directionY, actor.getSpeed(Creature.Move.HIDE));

                playerLastX = player.getX();
                playerLastY = player.getY();

                return false;
            }

            return false;
        }

        @Override
        public float getScore() {
            if (actor.getHP() <= hpHideValue) return 100;
            return 0;
        }


        private Vector2 getHideDirection() {
            Player player = game.getPlayers().first();

            float playerCurrentX = player.getX();
            float playerCurrentY = player.getY();
            int dirX = ((int) (actor.getHP()) % 2 == 1) ? -1 : 1;

            vec2.set(playerCurrentX - playerLastX, playerCurrentY - playerLastY);
            vec2.x *= dirX;
            vec2.y *= -1;
            vec2.nor();

            if (vec2.len() < 1) vec2.set(directionX, directionY); // old direction


            return vec2;


        }
    }

    public static class WaitForPlayer extends EnemyActivity {
        public WaitForPlayer(Creature actor) {
            super(actor, Activities.game);
        }

        @Override
        public boolean update(float delta) {
            return Distance(game.getPlayers().first(), actor) <= 400;
        }

        @Override
        public float getScore() {
            return 0;
        }
    }

    public static class AttackPlayer extends EnemyActivity {
        public AttackPlayer(Creature actor) {
            super(actor, Activities.game);
        }

        @Override
        public boolean update(float delta) {
            Player player = game.getPlayers().first();
            if (player.getActualHP() > 0 && Distance(actor, player) < 30) {
                TriggerAnimation(actor, "attack");
                return false;
            }

            return true;
        }

        @Override
        public float getScore() {
            return 0;
        }
    }

    public static class FearfulAttackPlayer extends EnemyActivity {

        private float hpFearValue;

        public FearfulAttackPlayer(Creature actor, float hpFearValue) {
            super(actor, Activities.game);
            this.hpFearValue = hpFearValue;
        }

        @Override
        public boolean update(float delta) {
            Player player = game.getPlayers().first();
            if (actor.getHP() >= hpFearValue && player.getActualHP() > 0 && Distance(actor, player) < 30) {
                TriggerAnimation(actor, "attack");
                return false;
            }

            return true;
        }

        @Override
        public float getScore() {
            if (actor.getHP() > hpFearValue) return 80;
            return 0;
        }
    }

}
