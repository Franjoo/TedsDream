package com.angrynerds.tedsdream.renderer;

import com.angrynerds.tedsdream.Layer;
import com.angrynerds.tedsdream.util.C;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Created by tim on 09.06.2014.
 */
public class MapLayerRenderer extends OrthogonalTiledMapRenderer{

    private OrthographicCamera fixedCamera;

    public MapLayerRenderer(TiledMap map, OrthographicCamera camera){
        super(map);

        this.setView(camera);
    }

    private void updateFixedCameraPosition(Layer layer) {
//        fixedCamera.position.x = camera.position.x * layer.getVelocityX() + layer.getX() + C.VIEWPORT_WIDTH / 2;
//        fixedCamera.position.y = camera.position.y * layer.getVelocityY() + layer.getY();
        fixedCamera.update();
    }

}
