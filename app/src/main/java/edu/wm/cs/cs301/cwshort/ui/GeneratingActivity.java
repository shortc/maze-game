package edu.wm.cs.cs301.cwshort.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import edu.wm.cs.cs301.cwshort.R;
import edu.wm.cs.cs301.cwshort.falstad.MazeController;
import edu.wm.cs.cs301.cwshort.falstad.MazeDataHolder;
import edu.wm.cs.cs301.cwshort.generation.MazeFactory;

/**
 * Created by cshort on 11/20/17.
 */

public class GeneratingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private String builder;
    private String driver;
    private int skill;
    public MazeController controller;
    private MazeFactory factory;
    Handler handler = new Handler();
    Thread t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);
        Intent grabIntent = getIntent();
        driver = (String) grabIntent.getStringExtra("Driver");
        builder = (String) grabIntent.getStringExtra("Builder");
        skill = (int) grabIntent.getIntExtra("Skill level", 0);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(false);// sets the inderterminate to false
        progressBar.setProgress(0); // sets the progressBar's progress to 0
        progressBar.setMax(100); // sets the max progress to 100 (default)
        progressStatus = 0;

        controller = new MazeController(this, skill, builder);
        //controller = (MazeController)getApplicationContext();

        controller.setPercentDone(0);
        controller.setBuilder(builder);
        controller.setSkillLevel(skill);
        controller.setPerfect(false);
        controller.setGeneratingActivity(this);

        MazeDataHolder.getInstance().setMazeController(controller);

        factory = new MazeFactory();
        factory.setGeneratingActivity(this);
        factory.order(controller); //should this be in a seperate thread?
        //Intent intentToPlay = new Intent(GeneratingActivity.this, PlayActivity.class);
        //intentToPlay.putExtra("Driver", driver);
        //Log.v("tag", "generating");




        // this thread shows the progression of the progress bar
        // without it the bar would be completed before the generating screen loads
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.v("tag", "running");

                while (progressStatus < 100) {
                    //handler.post(new Runnable() {
                      //  public void run() {
                            // increments the progress bar's progress
                            progressStatus = controller.getPercentDoneInt();
                            progressBar.setProgress(progressStatus);
                            Log.v("tag", String.valueOf(progressStatus));
                            if (progressStatus >= 100) {
                                Log.v("tag", "done");
                                // shows the progress bar
                                progressBar.setVisibility(View.VISIBLE);
                                //startActivity(new Intent(GeneratingActivity.this, PlayActivity.class));
                            }
                      //  }
                   // });
                }
                //start activity automatically is called after the generation is complete
                Intent intentToPlay = new Intent(GeneratingActivity.this, PlayActivity.class);
                intentToPlay.putExtra("Skill level", skill);
                intentToPlay.putExtra("Builder", builder);
                intentToPlay.putExtra("Driver", driver);
                startActivity(intentToPlay);
            }
        });
        // thread starts
        t.start();
    }

    public void playingActivity(){
        Intent intent5 = new Intent(GeneratingActivity.this, PlayActivity.class);
        intent5.putExtra("driver", driver);
        startActivity(intent5);
    }

    public void back(View view) {
        Log.v("tag", "continuing back to AMazeActivity state");
        // set the intent to prepare for state change
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }

    public void updateProgress(int percent){
        progressBar.setProgress(percent);
    }

    public int getProgress(){
        return progressBar.getProgress();
    }
}
