package com.angrynerds.tedsdream.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import sun.misc.IOUtils;

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
public class Server implements Disposable {

    private static String IP = null;
    public static final int PORT = 1234;
    public static final int MAX_PLAYERS = 4;

    private ServerSocketHints serverSocketHint;
    private Socket socket;
    private Array<ClientHandler> clients;


    public Server() {

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


    @Override
    public void dispose() {

    }


    public void broadcast(byte[] b, int k, int bytesRead){
        try {

            for (int i = 0; i < clients.size; i++) {
                clients.get(i).write(b);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private final class ClientHandler implements Runnable, Disposable {

        private final Socket connection;
        private final int id;

        private ObjectOutputStream oos;
        private ObjectInputStream ois;

        public ClientHandler(Socket connection, int id) {
            this.connection = connection;
            this.id = id;

            try {
                oos = new ObjectOutputStream(connection.getOutputStream());
                ois = new ObjectInputStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

//            // assign ID
//            write("I " + this.id);
//
//            // inform other players about new
//             playerAdded(this, this.id);
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

                        broadcast(buffer,0,bytesRead);

//                        output.write(buffer, 0, bytesRead);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }



//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//            try {
//
//                while (true) {
//
//                    final String line = reader.readLine();
//                    if (line != null) {
//
//                        // broadcast received message
//                        broadcast(this, line);
//                    }
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        public void write(byte[] buffer) throws IOException {
//            oos.write(buffer);
//            connection.getOutputStream().write(buffer,0,);
        }


        /**
         * writes a message to the output stream of the client
         *
         * @param message that that will be send
         */
        public void write(String message) {

            try {
                connection.getOutputStream().write((message + "\n").getBytes());
//                connection.getOutputStream().
//                connection.getOutputStream().write((byte[]) new Object());
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
