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
import android.widget.TextView;
import android.graphics.Color;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends AppCompatActivity {
    private static String s = "medium";
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
        timer.scheduleAtFixedRate(new Update(), 0, 10);
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

    public static void setS(String a){
        s = a;
    }

    public void radioClick(final View v){
        Button b = (Button)v;
        char key = b.getText().toString().charAt(0);

        switch(key){
            case 'S':
                cVas.size = 10;
                break;
            case 'M':
                cVas.size = 100;
                break;
            case 'L':
                cVas.size = 1000;
                break;
            case 'A':
                cVas.add = true;
                break;
            case 'R':
                cVas.add = false;
                break;
        }
    }
}



class CanvasView extends View {
    Particle[] particleArray = new Particle[64];
    private Path mPath;
    Context context;
    private Paint mPaint;
    public int size = 100;
    public boolean add = true;

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
        Particle.WIDTH = metrics.widthPixels;
        Particle.HEIGHT = metrics.heightPixels;
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        for (int i = 0; i < particleArray.length; i++){
            if (particleArray[i] != null)
                mPath.addCircle((int)particleArray[i].xPos, (int)particleArray[i].yPos, (float)(Math.sqrt(particleArray[i].mass)/5), Path.Direction.CCW);
        }
        canvas.drawPath(mPath, mPaint);
    }

    void physicsSim(){
        for (int i = 0; i < particleArray.length; i++)
            if (particleArray[i] != null)
                for (int j = i+1; j < particleArray.length; j++)
                    if (particleArray[j] != null)
                        if (Math.sqrt(Math.pow(particleArray[i].xPos-particleArray[j].xPos,2)+Math.pow(particleArray[i].yPos-particleArray[j].yPos,2)) > (Math.sqrt(particleArray[i].mass)+Math.sqrt(particleArray[j].mass))/5) {
                            Particle.updateVel(particleArray[i], particleArray[j]);
                        }
                        else {
                            Particle.merge(particleArray[i], particleArray[j]);
                            particleArray[j] = null;
                        }

        for (int i = 0; i < particleArray.length; i++)
            if (particleArray[i] != null)
                particleArray[i].updatePos();
    }

    private float x, y;
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
                    if (particleArray[i] == null) {
                        particleArray[i] = new Particle(event.getX(), event.getY(), (x-event.getX())/32, (y-event.getY())/32, size);
                        break;
                    }
                }
            }
        }
        return true;
    }
}