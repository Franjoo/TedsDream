package com.angrynerds.ui;

import com.angrynerds.game.screens.play.PlayController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TimeDisplay {


    private float x, y;
    private Texture texture_background;

    // font
    private BitmapFont font;

    // currentTime
    private double currentTime;
    private int seconds;
    private int minutes;
    private String time;
    private StringBuilder timeBuilder;

    // kills
    private String kills;

    private PlayController playController;

    public TimeDisplay(PlayController playController) {
        this.playController = playController;

        drawTimeBackground();

//        x = Gdx.graphics.getWidth() / 2 - texture_background.getWidth() / 2;
        x = Gdx.graphics.getWidth() - texture_background.getWidth() - 20;
        y = 15;

        // font
//        font = new BitmapFont(Gdx.files.internal("fonts/C64_white.fnt"), Gdx.files.internal("fonts/C64_0.png"), true);
//        font = new BitmapFont(Gdx.files.internal("fonts/arial-15.fnt"), Gdx.files.internal("fonts/arial-15.png"), true);
        font = new BitmapFont(Gdx.files.internal("fonts/bmtFont.fnt"), Gdx.files.internal("fonts/bmtFont.png"), true);
//        font = new BitmapFont(Gdx.files.internal("fonts/C64_black.fnt"), true);
        font.setColor(0.8f, 0.8f, 0.8f, 1);
        font.setScale(1.2f);


        time = new String();
        timeBuilder = new StringBuilder();
    }

    private void drawTimeBackground() {
        Pixmap p = new Pixmap(80, 30, Pixmap.Format.RGBA8888);
        p.setColor(0.2f, 0.2f, 0.2f, 0.4f);
        p.fillRectangle(0, 0, p.getWidth(), p.getHeight());
        p.setColor(1, 1, 1, 1);
        p.drawRectangle(0, 0, p.getWidth(), p.getHeight());

        texture_background = new Texture(p);
    }


    public void update(float delta) {
        buildTime(delta);

        buildKillsString();


    }

    private void buildKillsString() {
         kills = (playController.getWorld().getMap().getDeadEnemies() + " / " + playController.getWorld().getMap().getMaxEnemies());
    }

    private void buildTime(float delta) {
        currentTime += delta;
        seconds = (int) (currentTime) % 60;
        minutes = (int) (currentTime) / 60;

        // build time
        timeBuilder.delete(0, timeBuilder.length());
        if (minutes < 10) timeBuilder.append("0");
        timeBuilder.append(minutes);
        timeBuilder.append(" : ");
        if (seconds < 10) timeBuilder.append("0");
        timeBuilder.append(seconds);

        // set time string
        time = timeBuilder.toString();
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        // time
        batch.draw(texture_background, x, y);
        font.draw(batch, time, x + texture_background.getWidth() / 2 - font.getBounds(time).width / 2, y + texture_background.getHeight() / 2 - font.getBounds(time).height / 2);
        // kills
        batch.draw(texture_background, x, y + texture_background.getHeight() + 10);
        font.draw(batch, kills, x + texture_background.getWidth() / 2 - font.getBounds(time).width / 2, y + texture_background.getHeight() / 2 - font.getBounds(time).height / 2 + texture_background.getHeight() + 10);
        batch.end();
    }

}
