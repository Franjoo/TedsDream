package com.angrynerds.tedsdream.screens;

import com.angrynerds.tedsdream.ai.statemachine.Activities;
import com.angrynerds.tedsdream.ai.statemachine.Activity;
import com.angrynerds.tedsdream.camera.CameraHelper;
import com.angrynerds.tedsdream.core.Controller;
import com.angrynerds.tedsdream.events.AddPlayerEvent;
import com.angrynerds.tedsdream.events.AssignIDEvent;
import com.angrynerds.tedsdream.events.EnemyInitializationEvent;
import com.angrynerds.tedsdream.events.UpdatePlayerEvent;
import com.angrynerds.tedsdream.gameobjects.Creature;
import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.angrynerds.tedsdream.gameobjects.Player;
import com.angrynerds.tedsdream.gameobjects.PlayerRemote;
import com.angrynerds.tedsdream.gameobjects.items.Item;
import com.angrynerds.tedsdream.gameobjects.map.EnemyInitialization;
import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.angrynerds.tedsdream.input.IGameInputController;
import com.angrynerds.tedsdream.input.KeyboardInput;
import com.angrynerds.tedsdream.input.RemoteInput;
import com.angrynerds.tedsdream.ui.ControllerUI;
import com.angrynerds.tedsdream.ui.TimeDisplay;
import com.angrynerds.tedsdream.util.C;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Author: Franz Benthin
 */
public class GameController extends ScreenAdapter {

    private EnemyInitialization enemyInitialization;
    // TimeDisplay
    private TimeDisplay timer;

    // projection
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private CameraHelper cameraHelper;

    // map
    private Map map;

    private HashMap<Integer, PlayerRemote> playersRemotes;

    // game object arrays
    private Array<Creature> enemies;
    private Array<Creature> players;
    private Array<Item> items;
    private Player player;

    // y-sorting
    Array<GameObject> helperArray = new Array<>();
    Comparator<GameObject> comparatorY;

    // multiplayer
    private GameClient client;
    private StringBuilder stringBuilder = new StringBuilder();
    private String clientNames = "";

    private ControllerUI ui;

    private final Controller game;
    private final boolean isMultiplayer;
    private final boolean isServer;

    public GameController(Controller game, boolean isMultiplayer, boolean isServer) {
        this.game = game;
        this.isMultiplayer = isMultiplayer;
        this.isServer = isServer;

        // projection
        batch = new SpriteBatch();
        camera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);

        map = new Map(camera, game.tiledMap);
        playersRemotes = new HashMap<>();
        ui = new ControllerUI();

        // player input listener
        IGameInputController listener;
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            listener = ui.getListener();
        } else {
            listener = new KeyboardInput();
        }

        // create Activities for AI
        Activities.create(this);

        // add player
        player = new Player(listener, this);
        players.add(player);

        // server game settings
        if (this.isMultiplayer) {
            client = new GameClient(this);
        }

        // init enemies
        if (this.isServer || !this.isMultiplayer) {
            enemyInitialization = new EnemyInitialization(game.tiledMap,game.difficulty);
            map.initEnemies(enemyInitialization);
        }


        // camera
        cameraHelper = new CameraHelper();
        cameraHelper.applyTo(camera);
        cameraHelper.setTarget(player);

        // Y sorting comparator
        helperArray = new Array<GameObject>();
        comparatorY = new Comparator<GameObject>() {
            @Override
            public int compare(GameObject o1, GameObject o2) {
                if (o1.getY() < o2.getY()) return 1;
                else if (o1.getY() > o2.getY()) return -1;
                return 0;
            }
        };
    }

    public EnemyInitialization initialize(float diffulty){
                           return null;
    }



    public void update(float delta) {

//        timer.update(delta);

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            ui.update(delta);

        }

        player.update(delta);
        for (int i = 0; i <= playersRemotes.size(); i++) {
            if (playersRemotes.containsKey(i)) {
                playersRemotes.get(i).getPlayer().remoteUpdate(delta);
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
        if (isMultiplayer) {
            try {
                client.write(player.getUpdateEvent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zoom.apply(camera,delta);

    }

    @Override
    public void render(float delta) {
        update(delta);

        batch.setProjectionMatrix(camera.combined);

        map.renderForeground();

        batch.begin();

        // draw gameobjects ordered
        helperArray.clear();
        helperArray.addAll(players);
        helperArray.addAll(enemies);
        helperArray.addAll(items);
        helperArray.sort(comparatorY);

        for (int i = 0; i < helperArray.size; i++) {
            GameObject o = helperArray.get(i);
            Creature ownPlayer = players.get(0);
            if (Math.abs(ownPlayer.getX() - o.getX()) <= Gdx.graphics.getWidth()) {
//                shadowRenderer.drawShadows(batch, o);
                o.draw(batch);
            }
        }

        batch.end();

        map.renderBackground();

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            ui.render(delta);
        }

    }

    HashMap<Integer, PlayerRemote> getPlayerRemotes() {
        return playersRemotes;
    }

    public Array<Creature> getPlayers(){
        return players;
    }

    public Array<Creature> getEnemies() {
        return enemies;
    }

    public Array<Item> getItems() {
        return items;
    }

    void addPlayer(final int id) {

        RemoteInput remoteInput = new RemoteInput();
        Player player = new Player(remoteInput, this);
        player.setID(id);

        playersRemotes.put(id, new PlayerRemote(player, remoteInput));

        buildClientNameString();
        players.add(player);
        System.out.println("player added: " + id);
    }

    public void connect(final String host, final int port) {
        client.connect(host, port);

        System.out.println("isServer: " + isServer);

    }

    private void buildClientNameString() {
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append("Player ").append(player.getID()).append(" ( ME ) \n");

        playersRemotes.forEach(new BiConsumer<Integer, PlayerRemote>() {
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
     * <p/>
     * is responsible for server communication,
     * provides some methods to send game relevant data to server
     */
    private class GameClient implements Runnable, Disposable {

        private ObjectOutputStream oos;
        private ObjectInputStream ois;

        private Socket socket;

        private final GameController game;

        public GameClient(GameController game) {
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

            new Thread(this).start();
        }


        @Override
        public void run() {

            while (true) {

                try {

                    final Serializable event = (Serializable) ois.readObject();

                    // assign id
                    if (event instanceof AssignIDEvent) {
                        player.setID(((AssignIDEvent) event).id);
                    }

                    // add new player
                    else if (event instanceof AddPlayerEvent) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                game.addPlayer(((AddPlayerEvent) event).getId());
                            }
                        });

                        if (isServer) {
                            try {
                                client.write(enemyInitialization.getEnemyInitializationEvent());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    // update player
                    else if (event instanceof UpdatePlayerEvent) {
                        //((UpdatePlayerEvent) event).apply(game);
                        UpdatePlayerEvent e = (UpdatePlayerEvent) event;

                        Player player = game.getPlayerRemotes().get((e.getId())).getPlayer();

                        player.setPosition(e.getX(), e.getY());
                        player.setState(e.getAnimationState());
                        player.setFlip(e.isFlip());

                    }

                    // enemy initialization
                    else if (event instanceof EnemyInitializationEvent) {
                        System.out.println("INITIALIZATION RECEIVED");

                        Json json = new Json();
                        final EnemyInitialization initialization = json.fromJson(EnemyInitialization.class, ((EnemyInitializationEvent) event).getSerialization());
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                game.map.initEnemies(initialization);
                            }
                        });

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
