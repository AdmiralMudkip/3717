package ca.bcit.techpro.jason.physicssimulation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.graphics.Color;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends AppCompatActivity {
    //preset sizes for particles
    private static final int SMALL = 10;
    private static final int MEDIUM = 100;
    private static final int LARGE = 1000;
    private static int update = 10;
    private static int scenario = 0;
    static boolean valveTime = false;
    private CanvasView cVas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);

        if (update == 2){
            FrameLayout f = (FrameLayout)findViewById(R.id.Background);
            f.setBackgroundResource(R.drawable.gonetoplaid);
            setContentView(f);
        }

        cVas = (CanvasView) findViewById(R.id.canvas);
        cVas.size = MEDIUM;

        switch (scenario) {
            case 0:
                break;
            case 1: // binary system
                cVas.particleArray[0] = new Particle(CanvasView.SCREEN_WIDTH/2, CanvasView.SCREEN_HEIGHT/2-20, .6, 0, 1000);
                cVas.particleArray[1] = new Particle(CanvasView.SCREEN_WIDTH/2, CanvasView.SCREEN_HEIGHT/2+20, -.6, 0, 1000);
                break;
            case 2: // elliptical orbit
                cVas.particleArray[0] = new Particle(CanvasView.SCREEN_WIDTH/2, CanvasView.SCREEN_HEIGHT/2, 0, 0, 4000);
                cVas.particleArray[0].stationary = true;
                cVas.particleArray[1] = new Particle(CanvasView.SCREEN_WIDTH/2, CanvasView.SCREEN_HEIGHT/2+60, -1.4, 0, 100);
                break;
            case 3: // no escape
                cVas.particleArray[0] = new Particle(CanvasView.SCREEN_WIDTH/2, CanvasView.SCREEN_HEIGHT/2, 0, 0, 100000);
                cVas.particleArray[0].stationary = true;
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Update(), 0, update); //update, time to first update, update interval
    }

    class Update extends TimerTask {
        public void run() {
            cVas.physicsSim();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cVas.invalidate();
                }
            });
        }
    }

    public static void setUpdate(int i) { update = i; }
    public static void setScenario(int i) { scenario = i; }
    public static void updateValveTime() { valveTime = !valveTime; }

    // handle all the radio buttons
    public void radioClick(final View v){
        switch((String)v.getTag()){
            case "S":
                cVas.size = SMALL;
                break;
            case "M":
                cVas.size = MEDIUM;
                break;
            case "L":
                cVas.size = LARGE;
                break;
            case "A":
                cVas.add = true;
                break;
            case "R":
                cVas.add = false;
        }
    }

    // check boxes handlers
    public void toggleMerge(final View v){
        cVas.merge = !cVas.merge;
    }
    public void toggleStationary(final View v){
        cVas.stationary = !cVas.stationary;
    }
}


class CanvasView extends View {
    // used for calculating diameter of circle
    final double oneOverPi = 1/Math.PI;
    public static final int MAXIMUM_PARTICLES = 64;
    // scale the touches to remove, since the small particles are really small
    Particle[] particleArray = new Particle[MAXIMUM_PARTICLES];

    Context context;
    private Path mPath;
    private Paint mPaint;

    public int size;
    private float x, y;

    public static int SCREEN_WIDTH, SCREEN_HEIGHT, valveCounter = 0;

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
    }

    /** onDraw clears the path and then adds each circle to the path and then paints that path to the
     * canvas.  Allows for discrete draw steps, and having all particles update at the same time*/
    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        for (int i = 0; i < particleArray.length; i++){
            if (particleArray[i] != null) {
                mPath.addCircle((int) particleArray[i].xPosition, (int) particleArray[i].yPosition, (float) Math.sqrt(particleArray[i].mass * oneOverPi), Path.Direction.CCW);
            }
        }
        canvas.drawPath(mPath, mPaint);
    }

    // run through the entire loop,
    void physicsSim(){
        if (Game.valveTime && valveCounter++ == 2){
            valveCounter = 0;
            return;
        }
        // has to be done twice.  First, apply all the forces to everything from everything else
        for (int i = 0; i < particleArray.length-1; i++) {
            if (particleArray[i] != null) {
                for (int j = i + 1; j < particleArray.length; j++) {
                    if (particleArray[j] != null) {
                        if (Math.sqrt(Math.pow(particleArray[i].xPosition - particleArray[j].xPosition, 2) + Math.pow(particleArray[i].yPosition - particleArray[j].yPosition, 2)) > (Math.sqrt(particleArray[i].mass * oneOverPi) + Math.sqrt(particleArray[j].mass * oneOverPi))) {
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
                    if (particleArray[i] != null && (Math.pow(particleArray[i].xPosition - x, 2) + Math.pow(particleArray[i].yPosition - y, 2)) < (particleArray[i].mass*oneOverPi)) {
                        // i'd destroy the particle if i could, but java doesn't use destructors
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
                        particleArray[i] = new Particle(event.getX(), event.getY(), (x-event.getX())/32, (y-event.getY())/32, size);
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