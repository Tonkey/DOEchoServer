/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverTest;

import client.EchoClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import server.ClientHandler;
import server.EchoServer;

/**
 *
 * @author nickl
 */
public class ClientServerIntegrationTest {

    public ClientServerIntegrationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] args = new String[2];
                args[0] = "localhost";
                args[1] = "7777";
                EchoServer.main(args);
            }
        }).start();
    }

    @AfterClass
    public static void tearDownClass() {
        EchoServer.stopServer();
    }

    @Test
    public void send() throws IOException, InterruptedException {
        
        
        long waitTime = 1000;
        int count = 0;
        String msg = "Hello";
        EchoClient client = new EchoClient();
        client.connect("localhost", 7777);

        client.send(msg);

//        assertEquals("HELLO", client.receive());
        while (!client.hasChanged() && count < 10) {
            System.out.println("waiting for response from server");
            
                count++;
                Thread.sleep(waitTime);
            
        }
        
        
        //Test to see if the recieve method ran
        assertTrue(client.hasChanged());
        
        //Test to see if the msg recieved in recieve method is what we expected
        assertEquals(msg.toUpperCase() , client.returnedMsg);
        
        client.stop();
        //Test to see if the client has stopped
        assertTrue(client.isStopped);
    }
    
    @Test
    public void testMultipleConnections() throws IOException, InterruptedException{
    
        List<EchoClient> clients = new ArrayList();
        
        for(int i = 0; i < 5; i++){
            EchoClient client = new EchoClient();
            client.connect("localhost", 7777);
            clients.add(client);
        }
        
        for(EchoClient client : clients){
            client.send("Hello");
            
        }
        Thread.sleep(1000);
        for(int i = 0; i < clients.size(); i++){
            assertEquals("HELLO", clients.get(i).returnedMsg);
        }
        
        
    }
    
    
}
