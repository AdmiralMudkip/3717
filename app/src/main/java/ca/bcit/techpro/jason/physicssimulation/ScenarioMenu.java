package ca.bcit.techpro.jason.physicssimulation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ScenarioMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_scenario_menu);
    }

    public void onClick(final View v){
        switch((String)v.getTag()){
            case "1":
                Game.setScenario(1);
                break;
            case "2":
                Game.setScenario(2);
                break;
            case "3":
                Game.setScenario(3);
        }
        Intent i = new Intent(ScenarioMenu.this, Game.class);
        startActivity(i);
    }
}
