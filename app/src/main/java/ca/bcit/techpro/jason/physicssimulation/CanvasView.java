package ca.bcit.techpro.jason.physicssimulation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Jason on 2016-04-15.
 */

public class CanvasView extends View {
    // used for calculating diameter of circle

    public static final int MAXIMUM_PARTICLES = 64;
    // scale the touches to remove, since the small particles are really small
    Particle[] particleArray = new Particle[MAXIMUM_PARTICLES];

    Context context;
    private Path mPath;
    public Paint mPaint;

    public int size;
    private float x, y;

    public static int SCREEN_WIDTH, SCREEN_HEIGHT, FAT_FINGER_DISTANCE;
    public short valveCounter = 0;
    public boolean add = true, stationary = false, merge = true;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        // we set a new Path
        mPath = new Path();
        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);

        // get the screen size to adjust the bounds of the play area
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;

        // touch scaler, increase the touchable area to remove objects
        FAT_FINGER_DISTANCE = (int)(8*metrics.density);
    }

    /** onDraw clears the path and then adds each circle to the path and then paints that path to the
     * canvas.  Allows for discrete draw steps, and having all particles update at the same time*/
    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        for (int i = 0; i < particleArray.length; i++){
            if (particleArray[i] != null) {
                mPath.addCircle((int) particleArray[i].xPosition, (int) particleArray[i].yPosition, particleArray[i].radius, Path.Direction.CCW);
            }
        }
        canvas.drawPath(mPath, mPaint);
    }

    // run through the entire loop,
    void physicsSim(){
        // skip every third frame
        if (Game.valveTime && valveCounter++ == 2){
            valveCounter = 0;
            return;
        }
        // has to be done twice.  First, apply all the forces to everything from everything else
        for (int i = 0; i < particleArray.length-1; i++) {
            if (particleArray[i] != null) {
                for (int j = i + 1; j < particleArray.length; j++) {
                    if (particleArray[j] != null) {
                        if (Math.sqrt(Math.pow(particleArray[i].xPosition - particleArray[j].xPosition, 2) + Math.pow(particleArray[i].yPosition - particleArray[j].yPosition, 2)) > particleArray[i].radius + particleArray[j].radius) {
                            Particle.updateVelocity(particleArray[i], particleArray[j]);
                        } else if (merge) {
                            Particle.merge(particleArray[i], particleArray[j]);
                            particleArray[j] = null;
                        }
                    }
                }
            }
        }
        // secondly, update the positions
        for (int i = 0; i < particleArray.length; i++) {
            if (particleArray[i] != null) {
                particleArray[i].updatePos();
            }
        }
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
            if (!add){
                for (int i = 0; i < particleArray.length; i++) {
                    // calc the position of the touch relative to any object in the array
                    if (particleArray[i] != null && Math.sqrt((Math.pow(particleArray[i].xPosition - x, 2) + Math.pow(particleArray[i].yPosition - y, 2))) < particleArray[i].radius+FAT_FINGER_DISTANCE) {
                        // i'd destroy the particle if I could, but Java doesn't use destructors
                        // garbage collection pls
                        particleArray[i] = null;
                        break;
                    }
                }
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (add) {
                for (int i = 0; i < particleArray.length; i++) {
                    // little bit of optimization, put the new one in the first empty spot
                    if (particleArray[i] == null) {
                        // ctor to create a new particle, also checks the ending position, and adds
                        // force if there's a difference between the start and end positions
                        particleArray[i] = new Particle(x, y, (x-event.getX())/32, (y-event.getY())/32, size);
                        if (stationary) {
                            particleArray[i].stationary = true;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }
}
