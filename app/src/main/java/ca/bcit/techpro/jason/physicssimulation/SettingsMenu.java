package ca.bcit.techpro.jason.physicssimulation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingsMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings_menu);
    }

    public void onClick(final View v){
        Object c = v.getTag();
        switch((String) c){
            case "1":
                Game.setS("fast");
                return;
            case "2":
                Game.setS("medium");
                return;
            case "3":
                Game.setS("slow");
        }
    }
}
