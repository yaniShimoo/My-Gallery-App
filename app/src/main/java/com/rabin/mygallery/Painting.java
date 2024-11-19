package com.rabin.mygallery;

import android.net.Uri;

public class Painting {
    private int idP;
    private String nameP;
    private int widthP;
    private int heightP;
    private String dateP;
    private String descriptionP;
    private String paintingPic;
    private String uriP;
    private int costP;

    public Painting(int idP, String nameP, int widthP, int heightP, String dateP, String descriptionP, int costP) {
        this.idP = idP;
        this.nameP = nameP;
        this.widthP = widthP;
        this.heightP = heightP;
        this.dateP = dateP;
        this.paintingPic = "";
        this.descriptionP = descriptionP;
        this.costP = costP;
    }

    public Painting(int idP, String nameP, int widthP, int heightP, String dateP, String descriptionP, String paintingPic, int costP) {
        this.idP = idP;
        this.nameP = nameP;
        this.widthP = widthP;
        this.heightP = heightP;
        this.dateP = dateP;
        this.descriptionP = descriptionP;
        this.paintingPic = paintingPic;
        this.costP = costP;
    }

    public Painting(int idP, String nameP, int widthP, int heightP, String dateP, String descriptionP, String paintingPic, String uriP, int costP) {
        this.idP = idP;
        this.nameP = nameP;
        this.widthP = widthP;
        this.heightP = heightP;
        this.dateP = dateP;
        this.descriptionP = descriptionP;
        this.paintingPic = paintingPic;
        this.uriP = uriP;
        this.costP = costP;
    }

    public String getUriP() {
        return uriP;
    }

    public void setUriP(String uriP) {
        this.uriP = uriP;
    }

    public int getWidthP() {
        return widthP;
    }

    public void setWidthP(int widthP) {
        this.widthP = widthP;
    }

    public int getHeightP() {
        return heightP;
    }

    public void setHeightP(int heightP) {
        this.heightP = heightP;
    }

    public String getPaintingPic() {
        return paintingPic;
    }

    public void setPaintingPic(String paintingPic) {
        this.paintingPic = paintingPic;
    }

    public int getIdP() {
        return idP;
    }

    public void setIdP(int idP) {
        this.idP = idP;
    }

    public String getNameP() {
        return nameP;
    }

    public void setNameP(String nameP) {
        this.nameP = nameP;
    }

    public String getDateP() {
        return dateP;
    }

    public void setDateP(String dateP) {
        this.dateP = dateP;
    }

    public String getDescriptionP() {
        return descriptionP;
    }

    public void setDescriptionP(String descriptionP) {
        this.descriptionP = descriptionP;
    }

    public int getCostP() {
        return costP;
    }

    public void setCostP(int costP) {
        this.costP = costP;
    }

    @Override
    public String toString() {
        String res = this.widthP + " X " + this.heightP + "\n" + this.dateP + "\n" + this.costP + "₪" + "\n" + this.descriptionP;
        return res;
    }
}
