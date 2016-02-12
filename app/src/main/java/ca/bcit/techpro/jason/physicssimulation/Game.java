package ca.bcit.techpro.jason.physicssimulation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.TextureView;
import android.view.MotionEvent;
import android.widget.TextView;
import android.graphics.Point;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
public class Game extends AppCompatActivity {
    List<Point> pointList = new ArrayList<Point>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);
        TextView t = (TextView)findViewById(R.id.fullscreen_text);
        t.setText(s);
        ((TextureView)findViewById(R.id.textureView)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    pointList.add(new Point((int)event.getX(),(int)event.getY()));
                    Toast.makeText(Game.this,"Touch coordinates : [" + (int) event.getX() + "," + (int)event.getY() + "]", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    private static String s = "medium";

    public static void setS(String a){
        s = a;
    }

    public void onClick(final View view){
        System.out.println("goat");
    }
}
