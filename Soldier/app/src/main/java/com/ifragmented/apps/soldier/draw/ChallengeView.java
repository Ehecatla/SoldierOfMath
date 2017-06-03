package com.ifragmented.apps.soldier.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

/**
 * Created by Ella on 2017-01-10. ChallengeView is used to display mathematical challenges in Soldier
 * game. It enables moving rectangles with answer alternatives and animates them.
 */

public class ChallengeView extends View {

    private int chWidth =-1;
    private int chHeight =-1;
    private GRectManager manager;
    private GRectListener listener;
    private Paint paint;
    private Paint textPaint;

    private float chX;
    private float chY;
    private Canvas canvas;


    public ChallengeView(Context context) {
        super(context);
        this.createPaint();
    }

    public ChallengeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.createPaint();
    }

    public ChallengeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.createPaint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(chWidth== -1 || chHeight ==-1){
            chHeight = canvas.getHeight();
            chWidth = canvas.getWidth();

            //Log.d("TAG","width and height" +chWidth+ " "+ chHeight + " " +chX + " " + chY);
        }

        if(manager!=null){ //draw rectangles with text on them
            ArrayList<GameRectangle>rectangles = manager.getGameRectangles();
            if(rectangles.size()>0){
                textPaint.setTextSize((float) (rectangles.get(0).getRectWidth()/2));
               // Log.d("TAG","Text size is: " + textPaint.getTextSize());
            }
            for(GameRectangle rectangle: rectangles){
                drawRectangle(rectangle,canvas,paint,textPaint);
            }


            if(manager.getGameRectangles().size()>0) {
                if(manager.getGameRectangles().get(0).getRect().top > canvas.getHeight()){
                    destroyGRects();
                    listener.onGRectsOutOfView();
                } else {
                    animateRectangles();
                }
                //animateRectangles();
            }
        }
    }

    private void drawRectangle(GameRectangle rectangle, Canvas canvas, Paint paint, Paint textPaint){
        this.paint.setColor(rectangle.getRectangleColor().getColor());
        canvas.drawRect(rectangle.getRect(),paint);

        float x =(float)rectangle.getRect().left + rectangle.getRect().width()/4;
        float y = rectangle.getRect().top+rectangle.getRect().height()*3/4;
        canvas.drawText(rectangle.getTitle(), x, y ,textPaint);
    }

    public int getChWidth() {
        return chWidth;
    }
    public int getChHeight() {
        return chHeight;
    }

    /**
     * Creates number of rectangles with titles on them. The number is equal to number of rectangle
     * titles. It even takes listener that is used to send callbacks when some background animations
     * have been finished.
     * @param rectTitles array list of rectangle titles being answers to challenge question
     * @param listener listener that listens to callbacks after some animations has been fullfilled
     */
    public void createRectangles(ArrayList<String> rectTitles, GRectListener listener, int viewX, int viewY){
        this.listener = listener;
        ArrayList<GameRectangle> visibleRectangles = new ArrayList<>();
        int numberRect = rectTitles.size();
        int nrSpaces = numberRect-1;
        double recXYSize = chWidth/(double)numberRect;
        double space = (recXYSize*0.2);
        recXYSize*=0.8;

        int top=0;
        int right=(int)recXYSize;
        int left = 0;
        int bottom =(int)recXYSize;

        for(int i =0;i<numberRect;i++){

            if(i>0){
                left = (int)space + right;
                right = left + (int)recXYSize;
            } else {//helps place squares in the middle of screen
                left = (int)(space/2.0);
                right+=left;
            }
            Rect rect = new Rect(left,top,right,bottom);
            GameRectangle rectangle = new GameRectangle((int)recXYSize,(int)recXYSize,rectTitles.get(i),rect,RectangleColors.NEUTRAL);
            visibleRectangles.add(rectangle);
        }
        this.manager = new GRectManager(visibleRectangles);
    }


    public void colorRect(RectangleColors color, String gameRectangle){
        this.manager.changeColorFor(gameRectangle,color);
        this.invalidate();
        new Timer().schedule(new GrectTimeColorTask(manager,gameRectangle,RectangleColors.NEUTRAL) {
        }, 500);

    }

    private void createPaint(){
        this.paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(3);  //3
        this.textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(2);  //3
        paint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * Returns first rectangle string that intersect with given point in parameters. Null if none intersect.
     * @param x intersect x
     * @param y intersect y
     * @return String answer of rectangle that intersects given point
     */
    public String getIntersecting(float x, float y) {
        return manager.getAnswerFor(x,y);
    }

    public void animateRectangles() {
        GRectAnimator animator = new GRectAnimator(manager);
        animator.run();
    }

    /**
     * Removes all rectangles clearing challenge view. Invalidates view to ensure its redrawn.
     */
    public void destroyGRects() {
        manager.destroyRectangles();
        invalidate();
    }

    private class GRectAnimator implements Runnable{

        private GRectManager manager;

        public GRectAnimator(GRectManager manager){
            this.manager = manager;
        }

        @Override
        public void run() {
            manager.moveRectangles();
            postTask(this);
        }
    }

    private void postTask(GRectAnimator task){
        invalidate();
    }


    private class GrectTimeColorTask extends TimerTask {

        private String rectangle;
        private RectangleColors color;
        private GRectManager manager;

        public GrectTimeColorTask(GRectManager manager, String rectangle, RectangleColors color){
            this.manager = manager;
            this.rectangle = rectangle;
            this.color = color;
        }

        @Override
        public void run() {
            manager.changeColorFor(rectangle,color);
            postGTCTask();
        }
    }

    private void postGTCTask(){
        Looper.prepare();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                invalidate();
                listener.onColorAnimationDone();
            }
        });

    }




}
