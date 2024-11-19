package com.rabin.mygallery;

public class Customer {
    private int idC;
    private String nameC;
    private String phoneC;
    private String adressC;

    public Customer(int idC, String nameC, String phoneC, String adressC) {
        this.idC = idC;
        this.nameC = nameC;
        this.phoneC = phoneC;
        this.adressC = adressC;
    }

    public Customer(){
        this.idC = 0;
        this.nameC = "";
        this.phoneC = "";
        this.adressC = "";
    }

    public int getIdC() {
        return idC;
    }

    public void setIdC(int idC) {
        this.idC = idC;
    }

    public String getNameC() {
        return nameC;
    }

    public void setNameC(String nameC) {
        this.nameC = nameC;
    }

    public String getPhoneC() {
        return phoneC;
    }

    public void setPhoneC(String phoneC) {
        this.phoneC = phoneC;
    }

    public String getAdressC() {
        return adressC;
    }

    public void setAdressC(String adressC) {
        this.adressC = adressC;
    }

    @Override
    public String toString() {
        return phoneC + "\n"+ adressC;
    }
}
