package ca.bcit.techpro.jason.physicssimulation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SettingsMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings_menu);
    }

    // generic handler for all the buttons
    public void onClick(final View v){
        switch((String)v.getTag()){
            case "1": // slow
                Game.setUpdate(40);
                return;
            case "2": // medium
                Game.setUpdate(30);
                return;
            case "3": // fast
                Game.setUpdate(10);
                return;
            case "4": // ludicrous
                Game.setUpdate(5);
                return;
            case "5": // plaid
                Game.setUpdate(2);
                return;
            case "6": // valve time
                Game.updateValveTime();
                String s = (Game.valveTime) ? "enabled" : "disabled";
                Toast.makeText(this, "Valve time is " + s, Toast.LENGTH_SHORT).show();
        }
    }
}
