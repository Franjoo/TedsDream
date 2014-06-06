package com.angrynerds.tedsdream.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 21.11.13
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
public class Lifebar extends Actor {

//    private Player player;
    private Sprite border;
    private Sprite life;
    private Sprite bubble;
    private final float maxLifeWidth;

    public Lifebar (String borderName, String lifeName, String bubbleName){

//        this.player = player;
        this.bubble = new Sprite(new Texture("ui/ingame/bars/" + bubbleName));
        this.border = new Sprite(new Texture("ui/ingame/bars/" + borderName));
        this.life = new Sprite(new Texture("ui/ingame/bars/" + lifeName));
        maxLifeWidth = life.getWidth();
    }

    public Sprite getBubble() {
        return bubble;
    }

    public void setBubble(Sprite bubble) {
        this.bubble = bubble;
    }

    public Sprite getLife() {
        return life;
    }

    public void setLife(Sprite life) {
        this.life = life;
    }

    public Sprite getBorder() {
        return border;
    }

    public void setBorder(Sprite border) {
        this.border = border;
    }

    public void setLifePercent(float percent){
        life.setSize(maxLifeWidth*percent, life.getHeight());
    }

    @Override
    public float getHeight() {
        return border.getHeight();
    }

  //  @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.draw(life, getX()+bubble.getWidth()-19, getY(), life.getWidth(), life.getHeight());
        batch.draw(border, getX()+bubble.getWidth()-18, getY());
        batch.draw(bubble,getX(), getY());

        super.draw(batch, parentAlpha);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
