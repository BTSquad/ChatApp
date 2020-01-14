package com.oblivion.lightchat.Model;

public class ChatList_model {

  private String receiverUid;
  private String idChat;

  public ChatList_model(){

  }

  public ChatList_model(String receiverUid, String idChat) {
      this.receiverUid = receiverUid;
      this.idChat = idChat;
  }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }
}
