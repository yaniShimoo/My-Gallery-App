package com.rabin.mygallery;

public class Sketch {
    private int idS;
    private String nameS;
    private String dateS;
    private String descriptionS;
    private String sketchPic;

    public Sketch(int idS, String nameS, String dateS, String descriptionS, String sketchPic) {
        this.idS = idS;
        this.nameS = nameS;
        this.dateS = dateS;
        this.descriptionS = descriptionS;
        this.sketchPic = sketchPic;
    }
    public Sketch(int idS, String nameS, String dateS, String descriptionS){
        this.idS = idS;
        this.nameS = nameS;
        this.dateS = dateS;
        this.descriptionS = descriptionS;
        this.sketchPic = "";
    }

    public String getSketchPic() {
        return sketchPic;
    }

    public void setSketchPic(String sketchPic) {
        this.sketchPic = sketchPic;
    }

    public int getIdS() {
        return idS;
    }

    public void setIdS(int idS) {
        this.idS = idS;
    }

    public String getNameS() {
        return nameS;
    }

    public void setNameS(String nameS) {
        this.nameS = nameS;
    }

    public String getDateS() {
        return dateS;
    }

    public void setDateS(String dateS) {
        this.dateS = dateS;
    }

    public String getDescriptionS() {
        return descriptionS;
    }

    public void setDescriptionS(String descriptionS) {
        this.descriptionS = descriptionS;
    }

    @Override
    public String toString() {
        String s = "";
        //s = "Name: " + this.nameS;
        s += this.dateS + "\n"+ this.descriptionS;
        return s;
    }
}
