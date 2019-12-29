package com.oblivion.lightchat.Model;

public class Kontak_model {

    private String nama;
    private String nomor;

    public Kontak_model(){

    }

    public Kontak_model(String nama, String nomor) {
        this.nama = nama;
        this.nomor = nomor;
    }


    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }
}
