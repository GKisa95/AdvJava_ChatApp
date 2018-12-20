/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advjava_chatapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author GK
 */
public class ChatWindowController implements Initializable {

    @FXML
    private TextArea chatTextArea;
    @FXML
    private TextArea messageTextArea;
    @FXML
    private Button sendButton;
    @FXML
    private Label chatLabel;

    String myUsername;
    String otherUserName;
    ChatWindow cw;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        messageTextArea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                   sendMessage();
                    messageTextArea.setText("");
                    ke.consume(); 
                }
            }
        });
        
        
        
        messageTextArea.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                // yazıyor... mesaj
            }
        });
    }

    

    @FXML
    private void sendButtonHandler(ActionEvent event) {
        sendMessage();
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public void setMyUserName(String myUserName) {
        this.myUsername = myUserName;
    }

    public void setChatWindow(ChatWindow cw) {
        this.cw = cw;
        chatLabel.setText(myUsername + " => " + otherUserName);
    }

    public void sendMessage() {
        String message = messageTextArea.getText();
        if (message == null) {
            message = "";
        }
        ChatMessage cm = new ChatMessage(ChatMessage.MESSAGE, message, myUsername, otherUserName);
        cw.sendMessage(cm);
        chatTextArea.appendText(myUsername + ": \n   " + message + "\n");
        messageTextArea.setText(null);

    }

    
   
    public void showMessageOnChatArea(String message) {
        System.out.println("--------------------------------------");
        System.out.println("chat window controller of " + myUsername + " --- " + otherUserName);
        System.out.println(message);

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (message.equals(null)) {
                    chatTextArea.appendText(otherUserName + ": \n" + "" + "\n");
                } else {
                    chatTextArea.appendText(otherUserName + ": \n   " + message + "\n");
                }
            }
        });

    }


}
