package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private String ip;
    private int port;

    List<ClientHandler> clients = new ArrayList();
    
    public static void stopServer() {
        keepRunning = false;
    }

    public void send(String msg){
        for(ClientHandler client : clients){
            client.send(msg);
        }
    }
    
    public void removeHandler(ClientHandler ch){
        clients.remove(ch);
        String msg1 = "Client: " + ch.getName() + " disconnected";
        Logger.getLogger(Log.LOG_NAME).log(Level.INFO, msg1);
        String msg2 = "Remaining amount of clients connected: " + clients.size();
        Logger.getLogger(Log.LOG_NAME).log(Level.INFO, msg2);
    }
    
    private void runServer(String ip, int port) {
        this.port = port;
        this.ip = ip;
        Logger.getLogger(Log.LOG_NAME).log(Level.INFO, "Starting the Server");
        Logger.getLogger(Log.LOG_NAME).log(Level.INFO, "Server started. Listening on: " + port + ", bound to: " + ip);
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                ClientHandler client = new ClientHandler(socket, this);
                clients.add(client);
                Logger.getLogger(Log.LOG_NAME).log(Level.INFO, "Connected to a client");
                Logger.getLogger(Log.LOG_NAME).log(Level.INFO, "Current amount of clients connected: " + clients.size());
                

                client.start();
                
                
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(Log.LOG_NAME).log(Level.SEVERE, null, ex);
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                throw new IllegalArgumentException("Error: Use like: java -jar EchoServer.jar <ip> <port>");
            }
            String ip = args[0];
            int port = Integer.parseInt(args[1]);
            Log.setLogFile("logFile.txt", "ServerLog");
            new EchoServer().runServer(ip, port);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            Log.closeLogger();
        }
    }
}
