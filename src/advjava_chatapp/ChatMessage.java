/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advjava_chatapp;

import java.io.Serializable;

/**
 *
 * @author GK
 */
public class ChatMessage implements Serializable{

    static final int CONNECTED_CLIENTS = 0, MESSAGE = 1, LOGON = 2, LOGOFF = 3;
    private int type;
    private String message;
    private String from;
    private String to;

    public ChatMessage(int type, String message, String sender, String receiver) {
        this.type = type;
        this.message = message;
        this.from = sender;
        this.to = receiver;
    }

    public ChatMessage(int type, String message, String sender) {
        this.type = type;
        this.message = message;
        this.from = sender;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    public void setFrom(String from) {
        this.from = from;
    }
}
