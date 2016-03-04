package ca.bcit.techpro.jason.physicssimulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

public class Game extends AppCompatActivity {
    public static Body[] bodyList;
    private CanvasView cVas;

    public static final int LISTSIZE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);
        TextView t = (TextView)findViewById(R.id.fullscreen_text);
        t.setText(s);
        /*((TextureView)findViewById(R.id.canvas)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    pointList.add(new Point((int) event.getX(), (int) event.getY()));
                    Toast.makeText(Game.this, "Touch coordinates : [" + (int) event.getX() + "," + (int) event.getY() + "]", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });*/

        cVas = (CanvasView) findViewById(R.id.canvas);
        bodyList = new Body[LISTSIZE];
    }

    private static String s = "medium";

    public static void setS(String a){
        s = a;
    }

    public void onClick(final View view){
        System.out.println("goat");
    }
}


class Body {
    private float x, y, xVel, yVel;
    int mass;
    public static final double pi = 3.14159;

    public Body(float x, float y, int m){

    }


    static void updateVel() {
        for (int i = 0; i < Game.LISTSIZE - 1; i++) {
            for (int j = i + 1; j < Game.LISTSIZE - 1; j++) {
                double angle = Math.atan((Game.bodyList[i].y - Game.bodyList[j].y / (Game.bodyList[j].x - Game.bodyList[i].x)) * 180 / pi);

                double accel = Game.bodyList[j].mass * (Math.abs(Game.bodyList[i].x - Game.bodyList[j].x) * Math.abs(Game.bodyList[i].x - Game.bodyList[j].x) + Math.abs(Game.bodyList[i].y - Game.bodyList[j].y) * Math.abs(Game.bodyList[i].y - Game.bodyList[j].y));
                Game.bodyList[i].xVel += Math.cos(angle) * accel;
                Game.bodyList[i].yVel += Math.sin(angle) * accel;

                angle += 180;
                accel = accel / Game.bodyList[i].mass * Game.bodyList[j].mass;
                Game.bodyList[j].xVel += Math.cos(angle) * accel;
                Game.bodyList[j].yVel += Math.sin(angle) * accel;
            }
            Game.bodyList[i].x += Game.bodyList[i].xVel;
            Game.bodyList[i].y += Game.bodyList[i].yVel;
        }
    }


 class CanvasView extends View {

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
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        canvas.drawPath(mPath, mPaint);
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPath.lineTo(mX, mY);
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }

        Toast.makeText(getContext(), "Touch coordinates : [" + (int) event.getX() + "," + (int) event.getY() + "]", Toast.LENGTH_SHORT).show();
        return true;
    }
}