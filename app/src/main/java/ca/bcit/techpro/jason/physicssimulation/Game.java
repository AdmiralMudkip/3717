package ca.bcit.techpro.jason.physicssimulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.TextureView;
import android.view.MotionEvent;
import android.widget.TextView;
import android.graphics.Point;
import android.widget.Toast;
import android.graphics.Color;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends AppCompatActivity {
    private CanvasView cVas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);
        TextView t = (TextView)findViewById(R.id.fullscreen_text);
        t.setText(s);
        cVas = (CanvasView) findViewById(R.id.canvas);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Update(), 0, 17);
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
    private static String s = "medium";

    public static void setS(String a){
        s = a;
    }

    public void onClick(final View view){
        System.out.println("goat");
    }
}



class CanvasView extends View {
    Particle[] particleArray = new Particle[16];
    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    Context context;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;

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
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;

    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        mPath = new Path();
        physicsSim();
        for (int i = 0; i < particleArray.length; i++){
            if (particleArray[i] != null) {
                mPath.addCircle((int)particleArray[i].xPos, (int)particleArray[i].yPos, 10, Path.Direction.CCW);
            } else {
                break;
            }
        }
        canvas.drawPath(mPath, mPaint);
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(int x, int y) {
        for (int i = 0; i < particleArray.length; i++){
            if (particleArray[i] == null) {
                particleArray[i] = new Particle(x, y);
                break;
            }
        }
    }

    void physicsSim(){
        for (int i = 0; i < particleArray.length; i++) {
            for (int j = i+1; j < particleArray.length; j++) {
                if (particleArray[i] != null && particleArray[j] != null) {
                    double force = 10/Particle.distSq(particleArray[i], particleArray[j]);
                    double angle = Particle.getAngle(particleArray[i], particleArray[j]);
                    particleArray[i].xVel += force*Math.cos(angle);
                    particleArray[i].yVel += force*Math.sin(angle);
                    angle += 3.14159;
                    particleArray[j].xVel += force*Math.cos(angle);
                    particleArray[j].yVel += force*Math.sin(angle);
                } else {
                    break;
                }
            }
        }
        for (int i = 0; i < particleArray.length; i++) {
            if (particleArray[i] != null) {
                if (particleArray[i].xPos > width) {
                    particleArray[i].xPos = width;
                    particleArray[i].xVel = 0;
                } else if (particleArray[i].xPos < 0) {
                    particleArray[i].xPos = 0;
                    particleArray[i].xVel = 0;
                }
                if (particleArray[i].yPos > height) {
                    particleArray[i].yPos = height;
                    particleArray[i].yVel = 0;
                } else if (particleArray[i].yPos < 0) {
                    particleArray[i].yPos = 0;
                    particleArray[i].yVel = 0;
                }
                particleArray[i].updatePos();
            } else {
                break;
            }
        }
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startTouch((int) event.getX(), (int) event.getY());
        }
        return true;
    }
}