/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advjava_chatapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author GK
 */
public class Server_P {

    public static int numberOfClients = 0;
    private ArrayList<ClientThread> clientThreads;

    public Server_P() {
        clientThreads = new ArrayList<>();
    }
    
    public void start(){
        System.out.println("The chat server is running.");
        try {
            ServerSocket serverSocket = new ServerSocket(8000);            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("socket oluştu");
                ClientThread t = new ClientThread(socket);  
                clientThreads.add(t);                                  
                t.start();
                sendConnectedClientList();
            }

        } catch (IOException ex) {
            System.out.println("Socket exception");
        }
    }
    
    
    public void sendConnectedClientList(){
        System.out.println("sending the list");
        String message = "";
        for (ClientThread clientThread : clientThreads) {
            if (clientThread.logOn) {
                message += clientThread.getUsername() + ",";
            }
        }
        ChatMessage chatMessage = new ChatMessage(ChatMessage.CONNECTED_CLIENTS, message, "Server");
        for (ClientThread clientThread : clientThreads) {
            if (clientThread.logOn) {
                chatMessage.setTo(clientThread.getUsername());
                clientThread.sendToClient(chatMessage);
            }
        }        
    }
    
    public void forwardMessage(ChatMessage chatMessage) {
        for (int i = 0; i < clientThreads.size(); i++) {
            if (clientThreads.get(i).getUsername().equalsIgnoreCase(chatMessage.getTo())) {
                clientThreads.get(i).sendToClient(chatMessage);
            }
        }
    }
    
    public void clientLoggedOff(int id){
        for(int i = 0; i < clientThreads.size(); i++){
            if(clientThreads.get(i).id == id)
                clientThreads.remove(i);
        }
        sendConnectedClientList();
    }
    
    

    public static void main(String[] args) {
        Server_P server = new Server_P();
        server.start();
    }

    class ClientThread extends Thread {

        private int id;
        private String username;
        private ChatMessage chatMessage;
        boolean logOn;

        Socket socket;
        ObjectInputStream input;
        ObjectOutputStream output;

        ClientThread(Socket socket) {
            
            id = ++numberOfClients;
            this.socket = socket;
            
            try {                               
                output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();
                input = new ObjectInputStream(socket.getInputStream());                 
                
                
                chatMessage = (ChatMessage) input.readObject();
                if(chatMessage.getType() == ChatMessage.LOGON){
                    username = chatMessage.getMessage();
                    System.out.println(username + " connected.");
                    logOn = true;
                }
            } catch (IOException e) {
                System.out.println("ObjectStream exception 1");
            } catch (ClassNotFoundException ex) {
                System.out.println("Class not found exception");
            }
        }

        @Override
        public void run() {
            
            while (logOn) {
                try {
                    chatMessage = (ChatMessage) input.readObject();
                } catch (IOException e) {
                    System.out.println("ObjectStream exception 2");
                    break;
                } catch (ClassNotFoundException ex) {
                    System.out.println("Class not found exception");
                    break;
                }

                switch (chatMessage.getType()) {  

                    case ChatMessage.MESSAGE:
                        forwardMessage(chatMessage);
                        break;
                    case ChatMessage.LOGON:
                        //client loged off
                        System.out.println(username + " disconnected.");
                        logOn = false;
                        break;                    
                }
            }
            close();
            clientLoggedOff(id);
        }

        public boolean sendToClient(ChatMessage message) {

            if (!socket.isConnected()) {
                close();
                return false;
            }

            try {
                output.writeObject(message);
                System.out.println("Message send. From: " + message.getFrom() + " To: " + message.getTo());
            } catch (IOException e) {
                System.out.println("Send message exception to " + username);
            }
            return true;
        }

        private void close() {

            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
            }
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
            }
        }

        public String getUsername() {
            return username;
        }

    }

}
