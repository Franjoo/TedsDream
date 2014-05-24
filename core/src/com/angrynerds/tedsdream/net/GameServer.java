package com.angrynerds.tedsdream.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


public class GameServer implements Disposable {

    private static String IP = null;
    public static final int PORT = 1234;
    public static final int MAX_PLAYERS = 4;

    private ServerSocketHints serverSocketHint;
    private Socket socket;
    private Array<ClientHandler> clients;


    public GameServer() {

        // create client list with fixed capacity
        clients = new Array<>();

        printHosts();

    }

    public void start() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // create socket hint
                serverSocketHint = new ServerSocketHints();
                serverSocketHint.acceptTimeout = 0; // infinite

                // create server socket
                ServerSocket serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, PORT, serverSocketHint);
                System.out.println("server socket opened @" + PORT);

                // on client connect
                while (true) {

                    if (clients.size < MAX_PLAYERS) {
                        socket = serverSocket.accept(null);
                        ClientHandler clientHandler = new ClientHandler(socket, clients.size);

                        // create new Thread
                        new Thread(clientHandler).start();

                        // add to client list
                        clients.add(clientHandler);

                    }

                }

            }
        }).start();

    }

    /**
     * broadcast message to other clients
     *
     * @param handler that sends the message
     * @param message that will be send
     */

    public synchronized void broadcast(ClientHandler handler, String message) {
        for (ClientHandler ClientHandler : clients) {
            if (ClientHandler != handler) {
                ClientHandler.write(message);
            }
        }
    }

    /**
     * broadcast message to all clients
     *
     * @param message that will be send
     */
    public synchronized void broadcast(String message) {
        for (ClientHandler ClientHandler : clients) {
            ClientHandler.write(message);
        }
    }

    /**
     * broadcast that new client was added
     * inform new client about others
     *
     * @param self
     * @param id
     */
    public void playerAdded(ClientHandler self, int id) {

        // inform this player about other players
        for (ClientHandler other : clients) {
            if (other != self) {
                self.write("A " + other.getID());
            }
        }

        // inform other players about this player
        for (ClientHandler other : clients) {
            if (other != self) {
                other.write("A " + self.getID());
            }
        }

    }

    /**
     * prints all hosts that are found
     */
    public void printHosts() {

        List<String> addresses = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(interfaces)) {
                for (InetAddress address : Collections.list(ni.getInetAddresses())) {
                    if (address instanceof Inet4Address) {
                        addresses.add(address.getHostAddress());
                        System.out.println("host found: " + address.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void updateDebugInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            broadcast("S"); // start game
        } else if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            broadcast("E"); // end game
        }
    }

    @Override
    public void dispose() {
        for (ClientHandler client : clients) {
            client.dispose();
        }
    }

    public void startGame() {

    }


    class ClientHandler implements Runnable, Disposable {

        private Socket connection;
        private int id;

        public ClientHandler(Socket connection, int id) {
            this.connection = connection;
            this.id = id;

            // assign ID
            write("I " + this.id);

            // inform other players about new
            playerAdded(this, this.id);
        }

        /**
         * reads the input stream of the connected client
         */
        @Override
        public void run() {

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            try {

                while (true) {

                    final String line = reader.readLine();
                    if (line != null) {

                        // broadcast received message
                        broadcast(this, line);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * writes a message to the output stream of the client
         *
         * @param message that that will be send
         */
        public void write(String message) {

            try {
                connection.getOutputStream().write((message + "\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public int getID() {
            return id;
        }

        @Override
        public void dispose() {
            socket.dispose();
        }
    }

}
