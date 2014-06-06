package com.angrynerds.tedsdream.net;

import com.angrynerds.tedsdream.screens.game.AddPlayerEvent;
import com.angrynerds.tedsdream.screens.game.AssignIDEvent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Franjo
 * Date: 25.05.2014
 * Time: 15:10
 */
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
     * prints all hosts that are found
     */
    public void printHosts() {

        List<String> addresses = new ArrayList<>();
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


    @Override
    public void dispose() {

    }


    public void broadcast(byte[] b, int bytesRead, int id) {
        try {

            for (int i = 0; i < clients.size; i++) {
                if (clients.get(i).id != id)
                    clients.get(i).write(b, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private final class ClientHandler implements Runnable, Disposable {

        protected final Socket connection;
        protected final int id;

        protected ObjectOutputStream oos;
        protected ObjectInputStream ois;

        public ClientHandler(Socket connection, int id) {
            this.connection = connection;
            this.id = id;

            try {
                oos = new ObjectOutputStream(connection.getOutputStream());
                ois = new ObjectInputStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {

                // set player id
                oos.writeObject(new AssignIDEvent(this.id));

                // inform other players about this
                AddPlayerEvent addPlayerEvent = new AddPlayerEvent(this.id);
                for (int i = 0; i < clients.size; i++) {
                    ClientHandler handler = clients.get(i);
                    if (handler != this) {
                        handler.oos.writeObject(addPlayerEvent);
                    }
                }

                // inform this player about others
                for (int i = 0; i < clients.size; i++) {
                    ClientHandler handler = clients.get(i);
                    if (handler != this) {
                        oos.writeObject(new AddPlayerEvent(handler.id));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * reads the input stream of the connected client
         */
        @Override
        public void run() {

            try {

                while (true) {

                    byte[] buffer = new byte[1024]; // Adjust if you want
                    int bytesRead;
                    while ((bytesRead = connection.getInputStream().read(buffer)) != -1) {
                        broadcast(buffer, bytesRead, id);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        public void write(byte[] buffer, int count) throws IOException {
            connection.getOutputStream().write(buffer, 0, count);
        }

        @Override
        public void dispose() {
            socket.dispose();
        }
    }

}
