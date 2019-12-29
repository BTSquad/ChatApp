package com.oblivion.lightchat.Model;

public class Users_model {

    private String userId;
    private String namaUser;
    private String photoUrl;
    private String noTelp;


    public Users_model(){

    }

    public Users_model(String userId, String namaUser, String photoUrl, String noTelp) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.photoUrl = photoUrl;
        this.noTelp = noTelp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }
}
