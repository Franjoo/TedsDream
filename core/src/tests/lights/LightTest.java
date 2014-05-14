package tests.lights;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

/**
 * User: Franjo
 * Date: 13.12.13
 * Time: 12:02
 * Project: GameDemo
 */
public class LightTest extends ApplicationAdapter {

    SpriteBatch batch;
    OrthographicCamera camera;

    Texture texBackground;
    Texture texBlend;

    // light relevant attributes
    public static final float ambientIntensity = .7f;
    public static final Vector3 ambientColor = new Vector3(0.3f, 0.3f, 0.7f);

    ShaderProgram program;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);

        texBackground = new Texture(Gdx.files.internal("data/backgrounds/ground_brige_460p.png"));
        texBlend = new Texture(Gdx.files.internal("data/test/blendCircle.png"));


    }

    @Override
    public void render() {
        // background color
        Gdx.gl.glClearColor(0,0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        camera.update();

        batch.begin();
        batch.setColor(0.2f, 0.2f, 0.2f, 1);
        batch.draw(texBackground, 0, 0);

        batch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE_MINUS_SRC_ALPHA);
//        batch.setColor(1,1,1, 1);

        float y = Gdx.graphics.getHeight() - Gdx.input.getY() - texBlend.getHeight()/2;
        float x = Gdx.input.getX() - texBlend.getWidth()/2;

        batch.draw(texBlend,x,y);
        batch.end();

    }
}
