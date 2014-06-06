package com.angrynerds.tedsdream.particleEffects;

import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Tim on 16.05.2014.
 */
public class ParticleEffectExample extends GameObject {
    ParticleEffect effect;

    public ParticleEffectExample(ParticleEffect effect) {
        this.effect = effect;
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        effect.draw(batch); //define behavior when stage calls Actor.draw()
    }

    public ParticleEffect getEffect() {
        return effect;
    }


}
