package ca.bcit.techpro.jason.physicssimulation;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
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
    private static String s = "medium";
    private static int update = 10;
    private CanvasView cVas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);
        TextView t = (TextView)findViewById(R.id.fullscreen_text);
        t.setText(s);

        if (s == "plaid"){
            FrameLayout f = (FrameLayout)findViewById(R.id.Background);
            BitmapDrawable background = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.plaid));
            background.setTileModeX(Shader.TileMode.REPEAT);
            background.setTileModeY(Shader.TileMode.REPEAT);
            f.setBackgroundDrawable(background);

            setContentView(f);
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

    public static void setS(String a){
        s = a;
    }
    public static void setUpdate(int i) { update = i; }

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
    public static final int AMOUNT_OF_PARTICLES = 64;
    public static final double TOUCH_SCALER = 1.2;
    Particle[] particleArray = new Particle[AMOUNT_OF_PARTICLES];
    private Path mPath;
    Context context;
    private Paint mPaint;
    public int size = 100;
    private float x, y;
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
                mPath.addCircle((int)particleArray[i].xPos, (int)particleArray[i].yPos, (float)(Math.sqrt(particleArray[i].mass)/2), Path.Direction.CCW);
        }
        canvas.drawPath(mPath, mPaint);
    }

    void physicsSim(){
        for (int i = 0; i < particleArray.length; i++)
            if (particleArray[i] != null)
                for (int j = i+1; j < particleArray.length; j++)
                    if (particleArray[j] != null)
                        if (Math.sqrt(Math.pow(particleArray[i].xPos-particleArray[j].xPos,2)+Math.pow(particleArray[i].yPos-particleArray[j].yPos,2)) > (Math.sqrt(particleArray[i].mass)+Math.sqrt(particleArray[j].mass))/2) {
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
            } else {
                for (int i = 0; i < particleArray.length; i++) {
                    if (particleArray[i] != null && Math.abs(particleArray[i].xPos - x) < particleArray[i].mass * TOUCH_SCALER && Math.abs((particleArray[i].yPos - y)) < particleArray[i].mass * TOUCH_SCALER) {
                        particleArray[i] = null;
                        break;
                    }
                }
            }
        }
        return true;
    }
}