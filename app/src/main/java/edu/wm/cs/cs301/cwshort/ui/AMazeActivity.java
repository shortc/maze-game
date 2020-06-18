package edu.wm.cs.cs301.cwshort.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import edu.wm.cs.cs301.cwshort.R;

public class AMazeActivity extends AppCompatActivity {

    // these messages will be used to send data betwen states in P7
    public static final String SKILL_LEVEL = "edu.wm.cs.cs301.cwshort.amazebychristophershort.SKILL_LEVEL";
    public static final String DRIVER = "edu.wm.cs.cs301.cwshort.amazebychristophershort.DRIVER";
    public static final String BUILDER = "edu.wm.cs.cs301.cwshort.amazebychristophershort.BUILDER";

    Spinner spinner1, spinner2;
    SeekBar skillLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaze);

        skillLevel = (SeekBar) findViewById(R.id.skillBar);
        skillLevel.setProgress(0);
        skillLevel.incrementProgressBy(1);
        skillLevel.setMax(14);
        skillLevel = (SeekBar)findViewById(R.id.skillBar);
        final TextView skillBarValue = (TextView)findViewById(R.id.skillbartext);

        skillLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                skillBarValue.setText(String.valueOf(progress));
                skillLevel.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });


    spinner1 = (Spinner) findViewById(R.id.builderSpinner);
        // Create the Array Adapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.builder_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new SelectedListener());

        spinner2 = (Spinner) findViewById(R.id.driverSpinner);
        // Create the Array Adapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.driver_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new SelectedListener());
    }

    public class SelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selected = parent.getItemAtPosition(pos).toString();
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

//    public void onItemSelected(AdapterView<?> parent, View view,
//                               int pos, long id) {
//        String selected = parent.getItemAtPosition(pos).toString();
//
//    public void onNothingSelected(AdapterView<?> parent) {
//        // Another interface callback
//    }



    public void explore(View view) {
        Log.v("tag", "continuing using user-determined settings");
        // set the intent to prepare for state change
        Intent intent1 = new Intent(this, GeneratingActivity.class);
        intent1.putExtra("Skill level", skillLevel.getProgress());
        intent1.putExtra("Builder", String.valueOf(spinner1.getSelectedItem()));
        intent1.putExtra("Driver", String.valueOf(spinner2.getSelectedItem()));
        startActivity(intent1);
    }

    public void revisit(View view) {
        Log.v("tag", "continuing using previous settings for difficulty");
        // set the intent to prepare for state change
        Intent intent2 = new Intent(this, GeneratingActivity.class);
        startActivity(intent2);
    }
}
