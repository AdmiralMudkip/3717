package ca.bcit.techpro.jason.physicssimulation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);
        TextView t = (TextView)findViewById(R.id.fullscreen_text);
        t.setText(s);
    }

    private static String s = "medium";

    public static void setS(String a){
        s = a;
    }
}
