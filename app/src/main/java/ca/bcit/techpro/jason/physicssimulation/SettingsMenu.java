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
        switch((String)v.getTag()){
            case "1":
                Game.setUpdate(50);
                return;
            case "2":
                Game.setUpdate(30);
                return;
            case "3":
                Game.setUpdate(10);
                return;
            case "4":
                Game.setUpdate(5);
                return;
            case "5":
                Game.setUpdate(2);
        }
    }
}
