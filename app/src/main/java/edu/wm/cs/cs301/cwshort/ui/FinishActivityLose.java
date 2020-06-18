package edu.wm.cs.cs301.cwshort.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import edu.wm.cs.cs301.cwshort.R;

/**
 * Created by cshort on 11/20/17.
 */

public class FinishActivityLose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_lose);
        TextView energy =(TextView)findViewById(R.id.energy);
        energy.setText(getIntent().getStringExtra("energyUsed"));
        TextView path =(TextView)findViewById(R.id.path);
        path.setText(getIntent().getStringExtra("pathLength"));
    }

    public void backToStart(View view) {
        Log.v("tag", "continuing back to AMazeActivity state");
        // set the intent to prepare for state change
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }

    public void restart(View view) {
        Log.v("tag", "restarting current PlayActivity state");
        // set the intent to prepare for state change
        Intent intent = new Intent(this, GeneratingActivity.class);
        startActivity(intent);
    }
}
