package com.rabin.mygallery;

import com.airbnb.lottie.LottieAnimationView;

public class Order {
    private int idOrd;
    private String nameCusOrd;
    private String namePaintOrd;
    private String phoneOrd;
    private String dateOrd;
    private String timeOrd;
    private String adressOrd;
    private String priceOrd;
    private String complete;
    private String shipping;

    public Order(int idOrd, String namePaintOrd, String phoneOrd, String dateOrd, String timeOrd, String adressOrd, String priceOrd) {
        this.idOrd = idOrd;
        this.nameCusOrd = "";
        this.namePaintOrd = namePaintOrd;
        this.phoneOrd = phoneOrd;
        this.dateOrd = dateOrd;
        this.timeOrd = timeOrd;
        this.adressOrd = adressOrd;
        this.priceOrd = priceOrd;
        this.complete = "No";
        this.shipping = "No";
    }

    public int getIdOrd() {
        return idOrd;
    }

    public void setIdOrd(int idOrd) {
        this.idOrd = idOrd;
    }

    public String getNameCusOrd() {
        return nameCusOrd;
    }

    public void setNameCusOrd(String nameCusOrd) {
        this.nameCusOrd = nameCusOrd;
    }

    public String getNamePaintOrd() {
        return namePaintOrd;
    }

    public void setNamePaintOrd(String namePaintOrd) {
        this.namePaintOrd = namePaintOrd;
    }

    public String getDateOrd() {
        return dateOrd;
    }

    public void setDateOrd(String dateOrd) {
        this.dateOrd = dateOrd;
    }

    public String getTimeOrd() {
        return timeOrd;
    }

    public void setTimeOrd(String timeOrd) {
        this.timeOrd = timeOrd;
    }

    public String getAdressOrd() {
        return adressOrd;
    }

    public void setAdressOrd(String adressOrd) {
        this.adressOrd = adressOrd;
    }

    public String getPriceOrd() {
        return priceOrd;
    }

    public void setPriceOrd(String priceOrd) {
        this.priceOrd = priceOrd;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getPhoneOrd() {
        return phoneOrd;
    }

    public void setPhoneOrd(String phoneOrd) {
        this.phoneOrd = phoneOrd;
    }

    @Override
    public String toString() {
        String res = this.namePaintOrd + "\n"+ this.phoneOrd + "\n (" + this.nameCusOrd +")" + "\n" + this.dateOrd  + "\t" + "-" + "\t" + this.timeOrd;
        if (this.shipping != null && this.shipping.equals("Yes"))
            res = res + "\n" + adressOrd ;
        res += "\n" + " ₪" + this.priceOrd;
        return res;
    }
}
