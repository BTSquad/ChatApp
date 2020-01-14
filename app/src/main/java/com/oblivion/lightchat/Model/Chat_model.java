package com.oblivion.lightchat.Model;

public class Chat_model {

    private String sender_uid;
    private String receiver_uid;
    private String pesan;
    private String idMessage;

    public Chat_model(){

    }

    public Chat_model(String sender_uid, String receiver_uid, String pesan, String idMessage) {
        this.sender_uid = sender_uid;
        this.receiver_uid = receiver_uid;
        this.pesan = pesan;
        this.idMessage = idMessage;
    }

    public String getSender_uid() {
        return sender_uid;
    }

    public void setSender_uid(String sender_uid) {
        this.sender_uid = sender_uid;
    }

    public String getReceiver_uid() {
        return receiver_uid;
    }

    public void setReceiver_uid(String receiver_uid) {
        this.receiver_uid = receiver_uid;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }
}
