package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;
import java.util.Observable;

public class EchoClient extends Observable {

    Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;
    public boolean isStopped;

    List<Observer> observers = new ArrayList();

    
    public String returnedMsg;
    
    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
        isStopped = false;
        returnedMsg = "";
        new recieveTask(this).start();
    }

    public void send(String msg) {
        output.println(msg);
        new recieveTask(this).start();
    }

    public void stop() throws IOException {
        output.println(ProtocolStrings.STOP);
        isStopped = true;
    }

    public String receive() {
//        System.out.println("inside recieve");
        String msg = input.nextLine();
        if (msg.equals(ProtocolStrings.STOP)) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        returnedMsg=msg;
        setChanged();
        return msg;
    }

    //not used at the moment!
    public static void main(String[] args) {
        int port = 7777;
        String ip = "localhost";
        if (args.length == 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }
        try {
            EchoClient tester = new EchoClient();
            tester.connect(ip, port);
            
            
//            
//            System.out.println("Sending 'Hello world'");
//            tester.send("Hello World");
//            System.out.println("Waiting for a reply");
//            System.out.println("Received: " + tester.receive()); //Important Blocking call
//            
//            
//            
//            tester.notifyObservers(tester.receive());
            tester.stop();
            //System.in.read(); 
            
            
            
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void addObserver(Observer o){
        
        observers.add(o);
    }
    
    @Override
    public void notifyObservers(Object arg){
        
        for(Observer o : observers){
            o.update(this, arg);
        }
        
    }

}


class recieveTask extends Thread{
    
    EchoClient ec;
    
    public recieveTask(EchoClient ec){
        this.ec = ec;
    }
    
    @Override
    public void run()
    {
        System.out.println("inside recieve task");
        String msg = ec.receive();
        
        ec.notifyObservers(msg);
    }    
    
    
}