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
}

class CanvasView extends View {
    Particle[] particleArray = new Particle[8];
    private Path mPath;
    Context context;
    private Paint mPaint;

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

    void physicsSim(){
        for (int i = 0; i < particleArray.length; i++)
            for (int j = i+1; j < particleArray.length; j++)
                if (particleArray[i] != null && particleArray[j] != null)
                    Particle.updateVel(particleArray[i],particleArray[j]);
                else
                    break;

        for (int i = 0; i < particleArray.length; i++)
            if (particleArray[i] != null)
                particleArray[i].updatePos();
            else
                break;
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (int i = 0; i < particleArray.length; i++){
                if (particleArray[i] == null) {
                    particleArray[i] = new Particle(event.getX(), event.getY(), 1);
                    break;
                }
            }
        }
        return true;
    }
}