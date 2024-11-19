package com.rabin.mygallery;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;

public class Shape {

    private Path path;
    private int x;
    private int y;
    private String color;
    private float width;
    //private Path customShapePath;

    public Shape(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.path = new Path();
        this.width = 5f;
        this.path.moveTo(x, y);
        //this.customShapePath = new Path();
        //this.customShapePath.addCircle(x, y, 20, Path.Direction.CW);
        //this.customShapePath.moveTo(x, y);
    }
    public Shape(int x, int y, float width, String color){
        this.x = x;
        this.y = y;
        this.color = color;
        this.path = new Path();
        this.width = width;
        this.path.moveTo(x, y);
        //this.customShapePath = new Path();
        //this.customShapePath.addCircle(x, y, 20, Path.Direction.CW);
        //this.customShapePath.moveTo(x, y);
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.parseColor(this.color));
        paint.setStrokeWidth(this.width);
        canvas.drawPath(this.path, paint);
    }
    public void updatePoint(int _x, int _y){
        this.path.lineTo(_x, _y);
        //this.customShapePath.lineTo(_x, _y);
    }
    public float getStrokeWidth(){
        return this.width;
    }
    public void setStorkeWidth(float storkeWidth){
        this.width = storkeWidth;
    }
}
