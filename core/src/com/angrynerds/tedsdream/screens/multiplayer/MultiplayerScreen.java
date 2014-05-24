package com.angrynerds.tedsdream.screens.multiplayer;

import com.angrynerds.tedsdream.core.Controller;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Disposable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * User: Franjo
 */
public class MultiplayerScreen implements Screen {

    private GameClient client;

    private final Controller game;

    public MultiplayerScreen(Controller game) {
        this.game = game;

        client = new GameClient();
    }

    public void update(float delta) {

    }

    @Override
    public void render(float delta) {

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

    public void connect(final String host, final int port) {
        client.connect(host, port);
    }

    private void buildClientNames(){
        StringBuilder builder = new StringBuilder();

    }

    public String getClientNames() {
         return null;

    }

    /**
     * class GameClient
     * <p>
     * is responsible for server communication,
     * provides some methods to send game relevant data to server
     */
    private class GameClient implements Runnable, Disposable {

        // static Identification Prefixes
        public static final char POSITION = 'P';
        public static final char VELOCITY = 'V';
        public static final char ADD = 'A';
        public static final char ID = 'I';
        public static final char START = 'S';
        public static final char END = 'E';

        private Socket socket;

        public void connect(final String host, final int port) {
            SocketHints socketHints = new SocketHints();
            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, host, port, socketHints);
            System.out.println("server [" + host + "] connected @" + port);
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

//                                player.setID(id);
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
//                                        players[id] = new Player();
//                                        players[id].setID(id);
                                    }
                                });

                                System.out.println("player added: " + id);
                            }


                            // position
                            else if (line.charAt(0) == POSITION) {
                                final String args[] = line.split(" ");
                                final int id = Integer.parseInt(args[1]);
                                final float px = Float.parseFloat(args[2]);
                                final float py = Float.parseFloat(args[3]);

//                                players[id].setPosition(px, py);
                            }

                            // velocity
                            else if (line.charAt(0) == VELOCITY) {
                                final String args[] = line.split(" ");
                                final int id = Integer.parseInt(args[1]);
                                final float vx = Float.parseFloat(args[2]);
                                final float vy = Float.parseFloat(args[3]);

//                                players[id].setVelocity(vx, vy);
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
//            write(POSITION + " " + player.getID() + " " + player.getPosition().x + " " + player.getPosition().y);
        }

        public void writeVelocity() {
//            write(VELOCITY + " " + player.getID() + " " + player.getVelocity().x + " " + player.getVelocity().y);
        }

        /**
         * writes a message to the output stream of the client.
         * <p>
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


        @Override
        public void dispose() {
            socket.dispose();
        }
    }


}
