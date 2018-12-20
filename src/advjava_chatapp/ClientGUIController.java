/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advjava_chatapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author GK
 */
public class ClientGUIController extends Application implements Initializable {

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;
    ArrayList<ChatWindow> chatWindows;

    String username = "";
    String selectedConnectedUser = "";
    ClientGUIController cc ;
    
    ListenFromServer lfs;

    private ObservableList observableList;

    @FXML
    private Button startChatButton;
    @FXML
    private Button logOutButton;
    @FXML
    private ListView listView;
    @FXML
    private Label welcomeLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFrame frame = new JFrame();
        cc = this;
        while (username.isEmpty()) {
            username = JOptionPane.showInputDialog(frame, "Please enter a username", "Welcome to the chat room", JOptionPane.QUESTION_MESSAGE);
            if (username == null) {
                username = "";
            }
        }
        welcomeLabel.setText( "Welcome to chat room, " + username);
        startClient();

    }

    //starts a new chat
    @FXML
    private void startChatButtonHandler(ActionEvent event) {
        JFrame frame = new JFrame();
        selectedConnectedUser = listView.getSelectionModel().getSelectedItem().toString();
        

        System.out.println("selecteditem " + selectedConnectedUser);
        if (selectedConnectedUser.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select a username", "Username unselected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (ChatWindow cw : chatWindows) {
            if (cw.otherUserName.equals(selectedConnectedUser)) {
                JOptionPane.showMessageDialog(frame, "Chat window for " + selectedConnectedUser + " is already on screen. Please select another username", "Duplicate Chat Window ", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        ChatWindow chatWindow = new ChatWindow(username, selectedConnectedUser, this);
        chatWindows.add(chatWindow);
    }

    //log off
    @FXML
    private void logOutButtonHandler(ActionEvent event) {
        ChatMessage cm = new ChatMessage(ChatMessage.LOGOFF, username, username);
        try {
            output.writeObject(cm);
            close();
            lfs.stop();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(ClientGUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startClient() {
        System.out.println("in start");
        try {
            chatWindows = new ArrayList<>();
            socket = new Socket("localhost", 8000);

            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());

            ChatMessage chatMessage = new ChatMessage(ChatMessage.LOGON, username, username);
            System.out.println(chatMessage.getType() + "  :  " + chatMessage.getFrom());
            output.writeObject(chatMessage);
            System.out.println("chatmessage gönderildi");
            ChatMessage listMessage = (ChatMessage) input.readObject();
            if (listMessage != null) {
                showClientList(listMessage.getMessage());
            }
            lfs = new ListenFromServer();
            lfs.start();

        } catch (IOException ex) {
            System.out.println("ObjectStream exception.");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientGUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //send message to server
    public void sendMessage(ChatMessage chatMessage) {
        try {
            System.out.println(chatMessage.getType() + " " + chatMessage.getMessage() + " " + chatMessage.getFrom() + " " + chatMessage.getTo());
            output.writeObject(chatMessage);
            System.out.println("message send to server");
        } catch (IOException ex) {
            System.out.println("OutputStream exception.");
        }
    }

    //show connected client on listview
    public void showClientList(String clients) {

        observableList = FXCollections.observableArrayList();
        String[] clientList = clients.split(",");
        boolean isOnOnlineList = false;
        for(int i = 0; i < chatWindows.size(); i++){
            for(int j = 0; j < clientList.length; i++){
                if(chatWindows.get(i).myUsername.equals(clientList[j]))
                    isOnOnlineList = true;
            }
            if(!isOnOnlineList){    //client logged off
                chatWindows.get(i).close();
                chatWindows.remove(i);
            }
            isOnOnlineList = false;            
        }
        
        for (int i = 0; i < clientList.length; i++) {
            if (!clientList[i].equals(username)) {
                observableList.add(clientList[i]);
            }
        }
        if (observableList.isEmpty()) {
            System.out.println("Connected client list is empty");
            observableList.clear();
            listView.setItems(observableList);
        } else {
            for (int i = 0; i < observableList.size(); i++) {
                System.out.println(i + " " + observableList.get(i));
            }

            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    listView.setItems(observableList);
                }
            });
        }
        

    }

    public void close() {
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

    class ListenFromServer extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    ChatMessage chatMessage = (ChatMessage) input.readObject();

                    switch (chatMessage.getType()) {
                        case ChatMessage.CONNECTED_CLIENTS:
                            showClientList(chatMessage.getMessage());
                            break;
                        case ChatMessage.MESSAGE:
                            System.out.println("message received from " + chatMessage.getFrom());
                            forwardMessage(chatMessage);
                            
                            break;
                    }

                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println("Exception");
                }
            }
        }
    }

    public void forwardMessage(ChatMessage cm) {
        boolean isInTheList = false;
        for (ChatWindow chatWindow : chatWindows) {
            if (chatWindow.otherUserName.equals(cm.getFrom())) {
                System.out.println("new message old client");
                isInTheList = true;
                System.out.println("chat window of  " + chatWindow.myUsername + " --- " + chatWindow.otherUserName);
                chatWindow.showMessage(cm.getMessage());
                System.out.println(cm.getType() + " " + cm.getMessage() + " " + cm.getFrom() + " " + cm.getTo());
            }
        }

        if (!isInTheList) {   // bu mesajın geldiği kişi için chatWindow yoksa
            System.out.println("new message new client");
            ChatWindow cw = new ChatWindow(username, cm.getFrom(), cc, cm.getMessage());
            chatWindows.add(cw);
            System.out.println(cm.getType() + " " + cm.getMessage() + " " + cm.getFrom() + " " + cm.getTo());
        }

    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("clientGUI.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setResizable(false);
        //stage.getIcons().add(new Image(getClass().getResourceAsStream("chat_icon.png")));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
