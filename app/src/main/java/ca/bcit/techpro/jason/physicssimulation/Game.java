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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.graphics.Color;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends AppCompatActivity {
    private static int update = 10;
    private static int scenario = 0;
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

        switch (scenario) {
            case 0:
                break;
            case 1: // binary system

            case 2: // elliptical orbit

            case 3: // no escape
        }

        cVas = (CanvasView) findViewById(R.id.canvas);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Update(), 0, update); //update, time to first update, update interval
    }

    class Update extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cVas.physicsSim();
                    cVas.invalidate();
                }
            });
        }
    }

    public static void setUpdate(int i) { update = i; }
    public static void setScenario(int i) { scenario = i; }

    // handle all the radio buttons
    public void radioClick(final View v){
        switch((String)v.getTag()){
            case "S":
                cVas.size = 10;
                break;
            case "M":
                cVas.size = 100;
                break;
            case "L":
                cVas.size = 1000;
                break;
            case "A":
                cVas.add = true;
                break;
            case "R":
                cVas.add = false;
        }
    }

    public void toggleMerge(final View v){
        cVas.merge = !cVas.merge;
    }
    public void toggleStationary(final View v){
        cVas.stationary = !cVas.stationary;
    }
}


class CanvasView extends View {
    public static final int MAXIMUM_PARTICLES = 64;
    // scale the touches to remove, since the small particles are really small
    public static final double TOUCH_SCALER = 1.3;
    public static final double G = 0.5; // gravitational constant
    Particle[] particleArray = new Particle[MAXIMUM_PARTICLES];

    private Path mPath;
    Context context;
    private Paint mPaint;

    public int size = 100;
    private float x, y;

    public static int SCREEN_WIDTH, SCREEN_HEIGHT;

    public boolean add = true, stationary = false;
    public static boolean merge = true;

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
            if (particleArray[i] != null)
                mPath.addCircle((int)particleArray[i].xPos, (int)particleArray[i].yPos, (float)(Math.sqrt(particleArray[i].mass)/2), Path.Direction.CCW);
        }
        canvas.drawPath(mPath, mPaint);
    }

    // run through the entire loop,
    void physicsSim(){
        for (int i = 0; i < particleArray.length; i++)
            if (particleArray[i] != null)
                for (int j = i+1; j < particleArray.length; j++)
                    if (particleArray[j] != null)
                        if (Math.sqrt(Math.pow(particleArray[i].xPos-particleArray[j].xPos,2)+Math.pow(particleArray[i].yPos-particleArray[j].yPos,2)) > (Math.sqrt(particleArray[i].mass)+Math.sqrt(particleArray[j].mass))/2) {
                            Particle.updateVel(particleArray[i], particleArray[j]);
                        }
                        else if (merge){
                            Particle.merge(particleArray[i], particleArray[j]);
                            particleArray[j] = null;
                        }

        for (int i = 0; i < particleArray.length; i++)
            if (particleArray[i] != null)
                particleArray[i].updatePos();
    }


    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (add) {
                for (int i = 0; i < particleArray.length; i++) {
                    // little bit of optimization, put the new one in the first empty spot
                    if (particleArray[i] == null) {
                        particleArray[i] = new Particle(event.getX(), event.getY(), (x-event.getX())/32, (y-event.getY())/32, size);
                        break;
                    }
                }
            } else {
                for (int i = 0; i < particleArray.length; i++) {
                    // calc the position of the touch relative to any object in the array
                    if (particleArray[i] != null && Math.abs(particleArray[i].xPos - x) < particleArray[i].mass * TOUCH_SCALER && Math.abs((particleArray[i].yPos - y)) < particleArray[i].mass * TOUCH_SCALER) {
                        // i'd destroy the particle if i could, but java doesn't use destructors
                        // garbage collection pls
                        particleArray[i] = null;
                        break;
                    }
                }
            }
        }
        return true;
    }
}