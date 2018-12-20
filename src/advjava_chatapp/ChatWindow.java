/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advjava_chatapp;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author GK
 */
public class ChatWindow {

    String myUsername;
    String otherUserName;
    ArrayList<String> messages;
    ArrayList<ChatMessage> unsendMessages;
    ChatWindowController controller;
    ClientGUIController clientMain;

    public ChatWindow(String myUsername, String otherUserName, ClientGUIController clientMain) {
        this.myUsername = myUsername;
        this.otherUserName = otherUserName;
        this.clientMain = clientMain;
        ChatWindow cw = this;

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chatWindow.fxml"));
            Parent root = fxmlLoader.load();
            controller = fxmlLoader.<ChatWindowController>getController();
            controller.setOtherUserName(otherUserName);
            controller.setMyUserName(myUsername);
            controller.setChatWindow(cw);
            Stage chatStage = new Stage();
            Scene scene = new Scene(root);
            chatStage.setScene(scene);
            chatStage.show();
        } catch (IOException ex) {
            System.out.println("Chat Window oluşturulamadı.");
        }
    }

    public ChatWindow(String myUsername, String otherUserName, ClientGUIController clientMain, String message) {
        this.myUsername = myUsername;
        this.otherUserName = otherUserName;
        this.clientMain = clientMain;
        ChatWindow cw = this;

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chatWindow.fxml"));
                    Parent root = fxmlLoader.load();
                    controller = fxmlLoader.<ChatWindowController>getController();
                    controller.setOtherUserName(otherUserName);
                    controller.setMyUserName(myUsername);
                    controller.setChatWindow(cw);
                    Stage chatStage = new Stage();
                    Scene scene = new Scene(root);
                    chatStage.setScene(scene);
                    chatStage.show();
                    showMessageNewWindow(message);
                    
                } catch (IOException ex) {
                    System.out.println("Chat Window oluşturulamadı.");
                }
            }
        });
    }
    
    private void showMessageNewWindow(String message){
        controller.showMessageOnChatArea(message);
    }

    public void showMessage(String message) {
        System.out.println(message);
        if (controller == null) {
            System.out.println("controller null");
        }
        controller.showMessageOnChatArea(message);
    }

    public void sendMessage(ChatMessage message) {
        message.setFrom(myUsername);
        message.setTo(otherUserName);
        clientMain.sendMessage(message);
    }

    public void close() {
        System.exit(0);
    }

}
