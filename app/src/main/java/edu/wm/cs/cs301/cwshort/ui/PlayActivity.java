package edu.wm.cs.cs301.cwshort.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import edu.wm.cs.cs301.cwshort.R;
import edu.wm.cs.cs301.cwshort.falstad.BasicRobot;
import edu.wm.cs.cs301.cwshort.falstad.MazeController;
import edu.wm.cs.cs301.cwshort.falstad.MazeDataHolder;
import edu.wm.cs.cs301.cwshort.falstad.MazePanel;
import edu.wm.cs.cs301.cwshort.falstad.Pledge;
import edu.wm.cs.cs301.cwshort.falstad.Robot;
import edu.wm.cs.cs301.cwshort.falstad.RobotDriver;
import edu.wm.cs.cs301.cwshort.falstad.WallFollower;
import edu.wm.cs.cs301.cwshort.falstad.Wizard;

/**
 * Created by cshort on 11/20/17.
 */

public class PlayActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {



    public Robot robot;
    public RobotDriver robotDriver;
    public Handler walkingHandler, rotateHandler, graphicsHandler;

    MazePanel mazePan;
    MazeController controller;
    public boolean paused = false;
    public boolean isPlay = true;
    String victory;
    TextView battery;
    String hasWon;
    public String driver;
    public boolean cancel = false;
    public int pathLength = 0;
    Button up, down, left, right;
    ToggleButton pause, walls, exit, map, music;
    public ProgressBar progressBar;
    MediaPlayer good;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        driver = getIntent().getStringExtra("Driver");
        progressBar = (ProgressBar)findViewById(R.id.energyBar);
        progressBar.setMax(3500);
        progressBar.setProgress(3500);

        up = (Button) findViewById(R.id.up);
        down = (Button) findViewById(R.id.down);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);

        battery = (TextView)findViewById(R.id.textView5);
        walkingHandler = new Handler();
        rotateHandler = new Handler();
        graphicsHandler = new Handler();

        pause = (ToggleButton) findViewById(R.id.pause);
        walls = (ToggleButton) findViewById(R.id.walls);
        exit = (ToggleButton) findViewById(R.id.exit);
        map = (ToggleButton) findViewById(R.id.map);
        music = (ToggleButton) findViewById(R.id.musicToggle);

        up.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);

        walls.setOnCheckedChangeListener(this);
        exit.setOnCheckedChangeListener(this);
        map.setOnCheckedChangeListener(this);
        pause.setOnCheckedChangeListener(this);
        music.setOnCheckedChangeListener(this);

        battery = (TextView)findViewById(R.id.textView5);
        walkingHandler = new Handler();
        rotateHandler = new Handler();
        graphicsHandler = new Handler();

        good = MediaPlayer.create(PlayActivity.this, R.raw.good);
        //good.start();


        controller = MazeDataHolder.getInstance().getMazeController();

        //layout = findViewById(R.id.playActivity);
        mazePan = (MazePanel) findViewById(R.id.Panel);
        mazePan.init();
        //MazeDataHolder panel = MazeDataHolder.getInstance();
        //panel.setMazePanel(mazePan);

        controller.setPlayingActivity(this);
        controller.setPanel(mazePan);
        controller.setDriver(driver);
        robot = new BasicRobot(controller);
        controller.notifyViewerRedraw();
        resetMap();


        if(driver.equalsIgnoreCase("Manual")){
            up.setVisibility(View.VISIBLE);
            down.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
            pause.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            battery.setVisibility(View.VISIBLE);
        }
        else{
            up.setVisibility(View.INVISIBLE);
            down.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);
            left.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            battery.setVisibility(View.VISIBLE);

            //layout = (RelativeLayout)findViewById(R.id.playActivity);
//            mazePan = new MazePanel(this);
//            controller.setPlayingActivity(PlayActivity.this);
//            controller.setPanel(mazePan);
//            controller.setDriver(driver);
//            robot = new BasicRobot(controller);
//            controller.notifyViewerRedraw();
//            resetMap();
        }

        if(driver.equalsIgnoreCase("wizard")){
            robotDriver = new Wizard();
            robotDriver.setRobot(robot);
            robotDriver.setDistance(controller.getMazeConfiguration().getMazedists());
            robotDriver.setPlayingActivity(this);
            setMessage("Wizard Starting");
            Thread thread = new Thread(new wizardThread());
            thread.start();
        }

        if(driver.equalsIgnoreCase("wall follower")){
            robotDriver = new WallFollower();
            robotDriver.setRobot(robot);
            robotDriver.setDistance(controller.getMazeConfiguration().getMazedists());
            robotDriver.setPlayingActivity(this);
            setMessage("WallFollower starting");
            Thread thread = new Thread(new wallThread());
            thread.start();
        }

        if(driver.equalsIgnoreCase("pledge")){
            robotDriver = new Pledge();
            robotDriver.setRobot(robot);
            robotDriver.setDistance(controller.getMazeConfiguration().getMazedists());
            robotDriver.setPlayingActivity(this);
            setMessage("Pledge starting");
            Thread thread = new Thread(new pledgeThread());
            thread.start();
        }

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.up:
                setMessage("Up Button clicked");
                controller.keyDown('8');
                break;
            case R.id.left:
                setMessage("Left Button clicked");
                controller.keyDown('6');
                break;
            case R.id.right:
                setMessage("Right Button clicked");
                controller.keyDown('4');
                break;
            case R.id.down:
                setMessage("Down Button clicked");
                controller.keyDown('2');
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
            case R.id.pause:
                if (pause.isChecked()) {
                    setMessage("paused, press button to play");
                    paused = true;
                } else {
                    setMessage("game is playing, press button to pause");
                    paused = false;
                }
                break;
            case R.id.walls:
                controller.keyDown('m');
                setMessage("walls button clicked");
                break;
            case R.id.exit:
                controller.keyDown('s');
                setMessage("solution button clicked");
                break;
            case R.id.map:
                controller.keyDown('z');
                setMessage("maze button clicked");
                break;
            case R.id.musicToggle:
                if (music.isChecked()) {
                    good = MediaPlayer.create(PlayActivity.this, R.raw.good);
                    good.start();
                } else {
                    good.stop();
                }
                break;
        }
    }

    public void setMessage(String s){
        Log.v("PlayActivity", s);
    }

    public void pause(View view) {
        //pauses the game
        Log.v("tag", "game paused");
        CharSequence text = "Game Paused";
        // creates toast event to displays it
        Toast toast = Toast.makeText(PlayActivity.this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void toStart(View view) {
        Log.v("tag", "continuing back to AMazeActivity state");
        // set the intent to prepare for state change
        Intent intent = new Intent(PlayActivity.this, AMazeActivity.class);
        startActivity(intent);
    }

    public void left(View view) {
        Log.v("tag", "left key press");
        CharSequence text = "You went left!";
        //controller.turnLeft();
    }

    public void right(View view) {
        Log.v("tag", "right key press");
        CharSequence text = "You went right!";
        //controller.turnRight();
    }

    public void up(View view) {
        Log.v("tag", "up key press");
        CharSequence text = "You went forward!";
        //controller.goForward();
    }

    public void down(View view) {
        Log.v("tag", "down key press");
        CharSequence text = "You went back!";
        //controller.goBackward();
    }

    public void showMap(View view) {
        Log.v("tag", "show map press");
        CharSequence text = "Here's the map!";
        // creates toast event to displays it
        Toast toast = Toast.makeText(PlayActivity.this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showExit(View view) {
        Log.v("tag", "show exit press");
        CharSequence text = "Here's the exit!";
        // creates toast event to displays it
        Toast toast = Toast.makeText(PlayActivity.this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showWalls(View view) {
        Log.v("tag", "show walls press");
        CharSequence text = "Here's the walls!";
        // creates toast event to displays it
        Toast toast = Toast.makeText(PlayActivity.this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void resetMap(){
        final Resources resource = this.getResources();
        if(this.robotDriver != null) {
            this.updateBattery(((int) this.robotDriver.getRobot().getBatteryLevel()));
        }
        runOnUiThread(new Runnable() {
            public void run() {
                Bitmap bitMap = mazePan.getBitMap();
                Drawable background = new BitmapDrawable(resource, bitMap);
                //layout.setBackground(background);
            }
        });
    }

    public void updateBattery(int a){
        if(a < 0) {
            this.progressBar.setProgress(0);
        }
        this.progressBar.setProgress(a);
    }

    public void switchToFinish(){
        setMessage("Moving to finish screen: success");
        victory = "success";
        Intent intent2 = new Intent(this, FinishActivity.class);
        intent2.putExtra("Victory", victory);
        intent2.putExtra("energyUsed", String.valueOf(this.progressBar.getMax() - this.progressBar.getProgress()));
        intent2.putExtra("pathLength", String.valueOf(this.pathLength));
        startActivity(intent2);
    }

    public void switchToFinishLose(){
        setMessage("Moving to finish screen: failure");
        victory = "failure";
        Intent intent = new Intent(this, FinishActivityLose.class);
        intent.putExtra("Loss", victory);
        intent.putExtra("energyUsed", String.valueOf(this.progressBar.getMax() - this.progressBar.getProgress()));
        intent.putExtra("pathLength", String.valueOf(this.pathLength));
        startActivity(intent);
    }

    class wizardThread implements Runnable{
        public void run() {
            try {
                while (!controller.isOutside(controller.getPx(), controller.getPy()) && !cancel) {
                    if (!paused) {
                        Log.v("tag", "driving");
                        robotDriver.drive2Exit();
                    }
                }
                if (!cancel) {
                    Log.v("tag", "finsihed");
                    switchToFinish();
                }

            }catch(Exception e) {
                updateBattery(0);
                switchToFinishLose();
            }
        }
    }

    class wallThread implements Runnable{
        public void run() {
            try {
                while(!controller.isOutside(controller.getPx(), controller.getPy()) && !cancel) {
                    if (!paused) {
                        Log.v("tag", "driving");
                        robotDriver.drive2Exit();
                    }
                }
                if(!cancel){
                    Log.v("tag", "finsihed");
                    switchToFinish();
                }
            }catch(Exception e) {
                updateBattery(0);
                switchToFinishLose();
            }
        }
    }

    class pledgeThread implements Runnable{
        public void run() {
            try {
                while(!controller.isOutside(controller.getPx(), controller.getPy()) && !cancel) {
                    if (!paused) {
                        Log.v("tag", "driving");
                        robotDriver.drive2Exit();
                        if (progressBar.getProgress() <= 2 ){
                            switchToFinishLose();
                        }
                    }
                }
                if(!cancel){
                    Log.v("tag", "finsihed");
                    switchToFinish();
                }
            }catch(Exception e) {
                updateBattery(0);
                switchToFinishLose();
            }
        }
    }
}

