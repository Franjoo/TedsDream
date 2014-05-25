package com.angrynerds.tedsdream.screens.multiplayer;

import com.angrynerds.tedsdream.camera.CameraHelper;
import com.angrynerds.tedsdream.core.Controller;
import com.angrynerds.tedsdream.gameobjects.Player;
import com.angrynerds.tedsdream.gameobjects.PlayerRemote;
import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.angrynerds.tedsdream.input.KeyboardInput;
import com.angrynerds.tedsdream.input.RemoteInput;
import com.angrynerds.tedsdream.ui.TimeDisplay;
import com.angrynerds.tedsdream.util.C;
import com.angrynerds.tedsdream.util.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * User: Franjo
 */
public class MultiplayerScreen implements Screen {

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

    public MultiplayerScreen(Controller game) {
        this.game = game;

        client = new GameClient();

        batch = new SpriteBatch();
        timer = new TimeDisplay();
        camera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);

        // world objects
        map = new Map(camera);
        players = new HashMap<>();
        player = new Player(new KeyboardInput(), map);

        map.addPlayer(player);

        // camera
        cameraHelper = new CameraHelper();
        cameraHelper.applyTo(camera);
        cameraHelper.setTarget(player);

    }

    public void update(float delta) {

        timer.update(delta);

        player.update(delta);
        for (int i = 0; i <= players.size(); i++) {
            if (players.containsKey(i)) {
                players.get(i).getPlayer().update(delta);
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
        client.writePosition();
        client.writeState();

    }

    @Override
    public void render(float delta) {
        update(delta);

        batch.setProjectionMatrix(camera.combined);

        timer.render(batch);
        map.render(batch);
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

    private void addPlayer(final int id) {

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
        stringBuilder.append("Player " + player.getID() + " ( ME ) \n");

        players.forEach(new BiConsumer<Integer, PlayerRemote>() {
            @Override
            public void accept(Integer integer, PlayerRemote playerRemote) {
                stringBuilder.append("Player " + integer + "\n");
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

        // static Identification Prefixes
        public static final char POSITION = 'P';
        public static final char STATE = 'Q';
        public static final char ADD = 'A';
        public static final char ID = 'I';
        public static final char START = 'S';
        public static final char END = 'E';

        private String name = "";

        private Socket socket;

        public void connect(final String host, final int port) {
            SocketHints socketHints = new SocketHints();
            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, host, port, socketHints);
            System.out.println("server [" + host + "] connected @" + port);

            new Thread(this).start();
        }

        @Override
        public void run() {

            if (socket.isConnected()) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                try {

                    while (true) {

                        final String line = reader.readLine();

                        // parse received line
                        if (line != null) {

                            // start
                            if (line.charAt(0) == START) {
//                                started = true;
                            }

                            // end
                            else if (line.charAt(0) == END) {
//                                started = false;
                            }

                            // id
                            else if (line.charAt(0) == ID) {
                                final String args[] = line.split(" ");
                                final int id = Integer.parseInt(args[1]);

                                player.setID(id);
                                System.out.println("id assigned: " + id);
                            }

                            // add
                            else if (line.charAt(0) == ADD) {
                                final String args[] = line.split(" ");
                                final int id = Integer.parseInt(args[1]);

                                // post to main rendering thread, important!
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        addPlayer(id);
                                    }
                                });

                            }


                            // position
                            else if (line.charAt(0) == POSITION) {
                                final String args[] = line.split(" ");
                                final int id = Integer.parseInt(args[1]);
                                final float px = Float.parseFloat(args[2]);
                                final float py = Float.parseFloat(args[3]);


                                players.get(id).getPlayer().setPosition(px, py);
                            }

                            // state
                            else if (line.charAt(0) == STATE) {
                                final String args[] = line.split(" ");
                                final int id = Integer.parseInt(args[1]);
                                final int state = Integer.parseInt(args[2]);


                                players.get(id).getPlayer().setState(state);

//                                System.out.println("state received: " + state);
                            }


                            // System.out.println("message reseived: " + line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        public void writePosition() {
            write(POSITION + " " + player.getID() + " " + player.getX() + " " + player.getY());
        }

        public void writeState() {
            write(STATE + " " + player.getID() + " " + player.getState());
        }

        public void writeVelocity() {
//            write(VELOCITY + " " + player.getID() + " " + player.getVelocity().x + " " + player.getVelocity().y);
        }

        /**
         * writes a message to the output stream of the client.
         * <p/>
         * connect() should be called before writing a message!
         *
         * @param message that will be send
         */
        public void write(String message) {

            try {
                socket.getOutputStream().write((message + "\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public void dispose() {
            socket.dispose();
        }
    }


}
