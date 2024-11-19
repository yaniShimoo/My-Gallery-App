package com.rabin.mygallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class PaintView extends View {

    private Paint paint, bgPaint;
    private int xStart = 0, xEnd = 0, yStart = 0, yEnd = 0, radius = 1;
    private String currentColor = "#000000";
    private int color = Color.BLACK;
    private ArrayList<Shape> shapes = new ArrayList<>();
    Shape shape;
    private float w = 5;

    //private long lastDrawTime;
    //private float lastX, lastY;

    public PaintView(Context context) {
        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(w);
        bgPaint.setColor(Color.TRANSPARENT);
        //lastDrawTime = System.currentTimeMillis();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(bgPaint);
        for (Shape s : shapes) {
            s.draw(canvas, paint);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //long currentTime = System.currentTimeMillis();
        //float drawingSpeed = calculateDrawingSpeed(motionEvent.getX(), motionEvent.getY(), currentTime);

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            shape = new Shape((int) motionEvent.getX(), (int) motionEvent.getY(), w, currentColor);
            shapes.add(shape);
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            shape.updatePoint((int) motionEvent.getX(), (int) motionEvent.getY());
            //shape.setStorkeWidth(w);
            invalidate();
        }
        //lastX = motionEvent.getX();
        //lastY = motionEvent.getY();
        //lastDrawTime = currentTime;
        return true;
    }

    /* private float calculateDrawingSpeed(float x, float y, long currentTime) {
        float distance = (float) Math.sqrt(Math.pow(x - lastX, 2) + Math.pow(y - lastY, 2));
        long deltaTime = currentTime - lastDrawTime;
        return distance / deltaTime;
    } */

    public void setCurrentPoint(String shape) {
        currentColor = "#AAAAAA";
    }

    public void setCurrentColor(String color) {
        currentColor = color;
    }

    public void undoShape() {
        if (shapes.size() > 0) {
            shapes.remove(shapes.size() - 1);
        }
    }

    public void setWidth(float width) {
        w = width;
        if (shape != null){
            shape.setStorkeWidth(w);
        }
    }


}
