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

        cVas = (CanvasView) findViewById(R.id.canvas);
        cVas.size = MEDIUM;

        // gone to plaid enabled
        if (update == 2){
            FrameLayout f = (FrameLayout)findViewById(R.id.Background);
            cVas.mPaint.setColor(Color.WHITE);
            f.setBackgroundResource(R.drawable.gonetoplaid);
            setContentView(f);
        }

        // load particle array with preset data based on the loaded scenario
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
