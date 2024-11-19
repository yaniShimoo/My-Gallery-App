package com.rabin.mygallery;

public class Message {
    private int idM;
    private String textM;
    private String dateM;
    private String phoneM;

    public Message(int idM, String textM, String dateM, String phoneM) {
        this.idM = idM;
        this.textM = textM;
        this.dateM = dateM;
        this.phoneM = phoneM;
    }

    public int getIdM() {
        return idM;
    }

    public void setIdM(int idM) {
        this.idM = idM;
    }

    public String getTextM() {
        return textM;
    }

    public void setTextM(String textM) {
        this.textM = textM;
    }

    public String getDateM() {
        return dateM;
    }

    public void setDateM(String dateM) {
        this.dateM = dateM;
    }

    public String getPhoneM() {
        return phoneM;
    }

    public void setPhoneM(String phoneM) {
        this.phoneM = phoneM;
    }

    @Override
    public String toString() {
        return dateM+ "\n"+ phoneM+ "\n"+ textM;
    }
}
