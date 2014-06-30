package com.angrynerds.tedsdream.screens.game;

import com.angrynerds.tedsdream.camera.CameraHelper;
import com.angrynerds.tedsdream.core.Controller;
import com.angrynerds.tedsdream.gameobjects.Player;
import com.angrynerds.tedsdream.gameobjects.PlayerRemote;
import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.angrynerds.tedsdream.gameobjects.map.EnemyInitialization;
import com.angrynerds.tedsdream.input.KeyboardInput;
import com.angrynerds.tedsdream.input.RemoteInput;
import com.angrynerds.tedsdream.ui.ControllerUI;
import com.angrynerds.tedsdream.ui.TimeDisplay;
import com.angrynerds.tedsdream.util.C;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

import java.io.*;
import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * User: Franjo
 */
public class _MPGame implements Screen {

    private EnemyInitialization enemyInitialization;
    // TimeDisplay
    private TimeDisplay timer;

    // camera
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private CameraHelper cameraHelper;

    // map
    private Map map;

    private HashMap<Integer, PlayerRemote> players;
    private Player player;

    // multiplayer
    private GameClient client;
    private StringBuilder stringBuilder = new StringBuilder();
    private String clientNames = "";

    private final Controller game;
    private final boolean isServer;

    private ControllerUI ui;

    public _MPGame(Controller game, boolean isServer) {
        this.game = game;
        this.isServer = isServer;

        client = new GameClient(this);

        batch = new SpriteBatch();
        timer = new TimeDisplay();
        camera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);

        map = new Map(camera, game.tiledMap);

        // world objects
        if (isServer) {
            enemyInitialization = new EnemyInitialization(game.tiledMap);
            map.initEnemies(enemyInitialization);
        }

        players = new HashMap<>();

        ui = new ControllerUI();
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            player = new Player(ui.getListener(), map);

        } else {
            player = new Player(new KeyboardInput(), map);
        }

        map.addPlayer(player);

        // camera
        cameraHelper = new CameraHelper();
        cameraHelper.applyTo(camera);
        cameraHelper.setTarget(player);

    }

    public void update(float delta) {

        timer.update(delta);

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            ui.update(delta);

        }

        player.update(delta);
        for (int i = 0; i <= players.size(); i++) {
            if (players.containsKey(i)) {
                players.get(i).getPlayer().remoteUpdate(delta);
            }
        }

        map.update(delta);

        // camera
        camera.update();
        cameraHelper.update(delta);
        cameraHelper.applyTo(camera);

        // menu on player dead
        if (player.getActualHP() <= 0) {
            game.setScreen(game.mainMenu);
        }


        // send game relevant data
        try {
            client.write(player.getUpdateEvent());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(float delta) {
        update(delta);

        batch.setProjectionMatrix(camera.combined);

        timer.render(batch);
        map.render(batch);


        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            ui.render(delta);
        }

    }

    HashMap<Integer, PlayerRemote> getPlayers() {
        return players;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    void addPlayer(final int id) {

        RemoteInput remoteInput = new RemoteInput();
        Player player = new Player(remoteInput, map);
        player.setID(id);

        players.put(id, new PlayerRemote(player, remoteInput));

        buildClientNameString();
        map.addPlayer(player);
        System.out.println("player added: " + id);
    }

    public void connect(final String host, final int port) {
        client.connect(host, port);
    }

    private void buildClientNameString() {
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append("Player ").append(player.getID()).append(" ( ME ) \n");

        players.forEach(new BiConsumer<Integer, PlayerRemote>() {
            @Override
            public void accept(Integer integer, PlayerRemote playerRemote) {
                stringBuilder.append("Player ").append(integer).append("\n");
            }
        });


        clientNames = stringBuilder.toString();
    }

    public String getClientNames() {
        return clientNames;
    }

    /**
     * class GameClient
     * <p>
     * is responsible for server communication,
     * provides some methods to send game relevant data to server
     */
    private class GameClient implements Runnable, Disposable {

        private ObjectOutputStream oos;
        private ObjectInputStream ois;

        private Socket socket;

        private final _MPGame game;

        public GameClient(_MPGame game) {
            this.game = game;
        }

        public void connect(final String host, final int port) {
            SocketHints socketHints = new SocketHints();
            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, host, port, socketHints);
            System.out.println("server [" + host + "] connected @" + port);

            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

          //  new Thread(this).start();
        }


        @Override
        public void run() {

            while (true) {

                try {

                    Event event = (Event) ois.readObject();

                    if (event instanceof AssignIDEvent) ((AssignIDEvent) event).apply(player);
                    else if (event instanceof AddPlayerEvent) {
                        ((AddPlayerEvent) event).apply(game);
//                        if (isServer) {
//                            try {
//                              //  client.write(new EnemyInitializationEvent(enemyInitialization.getSerializationString()));
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }

                    }
                    else if (event instanceof UpdatePlayerEvent) ((UpdatePlayerEvent) event).apply(game);


//                    if (event instanceof GameEvent)
//                        ((GameEvent) event).apply(game);
//
//                    else if (event instanceof PlayerEvent)
//                        ((PlayerEvent) event).apply(player);

                    else if (event instanceof EnemyInitializationEvent) {
                        Json json = new Json();
                        EnemyInitialization s = json.fromJson(EnemyInitialization.class, ((EnemyInitializationEvent) event).getSerialization());
                        game.map.initEnemies(s);

                        System.out.println("INIT RECEIVED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(Serializable obj) throws IOException {
            oos.reset();
            oos.writeObject(obj);
        }

        @Override
        public void dispose() {
            socket.dispose();
        }
    }


}
