package edu.wm.cs.cs301.cwshort.falstad;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Iterator;

import edu.wm.cs.cs301.cwshort.falstad.Constants.StateGUI;
import edu.wm.cs.cs301.cwshort.generation.CardinalDirection;
import edu.wm.cs.cs301.cwshort.generation.Cells;
import edu.wm.cs.cs301.cwshort.generation.Factory;
import edu.wm.cs.cs301.cwshort.generation.MazeConfiguration;
import edu.wm.cs.cs301.cwshort.generation.MazeContainer;
import edu.wm.cs.cs301.cwshort.generation.Order;
import edu.wm.cs.cs301.cwshort.ui.GeneratingActivity;
import edu.wm.cs.cs301.cwshort.ui.PlayActivity;

/**
 * Class handles the user interaction.
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user.
 * At this point user keyboard input is first dealt with a key listener (SimpleKeyListener)
 * and then handed over to a MazeController object by way of the keyDown method.
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class MazeController implements Order {
    // Follows a variant of the Model View Controller pattern (MVC).
    // This class acts as the controller that gets user input and operates on the model.
    // A MazeConfiguration acts as the model and this class has a reference to it.
    protected MazeConfiguration mazeConfig;
    // Deviating from the MVC pattern, the controller has a list of viewers and
    // notifies them if user input requires updates on the UI.
    // This is normally the task of the model in the MVC pattern.

    // views is the list of registered viewers that get notified
    final private ArrayList<Viewer> views = new ArrayList<Viewer>();
    // all viewers share access to the same graphics object, the panel, to draw on
    public MazePanel panel;

    // state keeps track of the current GUI state, one of STATE_TITLE,...,STATE_FINISH, mainly used in redraw()
    private StateGUI state;
    // possible values are defined in Constants
    // user can navigate
    // title -> generating -(escape) -> title
    // title -> generation -> play -(escape)-> title
    // title -> generation -> play -> finish -> title
    // STATE_PLAY is the main state where the user can navigate through the maze in a first person view

    private int percentdone = 0; 		// describes progress during generation phase
    protected boolean showMaze;		 	// toggle switch to show overall maze on screen
    protected boolean showSolution;		// toggle switch to show solution in overall maze on screen
    protected boolean mapMode; // true: display map of maze, false: do not display map of maze
    // map_mode is toggled by user keyboard input, causes a call to draw_map during play mode

    // current position and direction with regard to MazeConfiguration
    private int px, py; // current position on maze grid (x,y)
    private int dx, dy;  // current direction

    // current position and direction with regard to graphics view
    // graphics has intermediate views for a smoother experience of turns
    private int viewx, viewy; // current position
    private int viewdx, viewdy; // current view direction, more fine grained than (dx,dy)
    private int angle; // current viewing angle, east == 0 degrees
    //static final int viewz = 50;
    private int walkStep; // counter for intermediate steps within a single step forward or backward
    private Cells seencells; // a matrix with cells to memorize which cells are visible from the current point of view
    // the FirstPersonDrawer obtains this information and the MapDrawer uses it for highlighting currently visible walls on the map

    // about the maze and its generation
    protected int skill; // user selected skill level, i.e. size of maze
    protected Builder builder; // selected maze generation algorithm
    protected boolean perfect; // selected type of maze, i.e.
    // perfect == true: no loops, i.e. no rooms
    // perfect == false: maze can support rooms

    // The factory is used to calculate a new maze configuration
    // The maze is computed in a separate thread which makes
    // communication with the factory slightly more complicated.
    // Check the factory interface for details.
    public Factory factory;

    // Filename if maze is loaded from file
    protected String filename;

    //private int zscale = Constants.VIEW_HEIGHT/2;
    private RangeSet rset;

    private boolean deepdebug = false;
    private boolean allVisible = false;
    private boolean newGame = false;
    public String driver;
    public BasicRobot robot;
    Context context;
    ProgressBar progressBar;
    GeneratingActivity gen;
    PlayActivity play;
    boolean buffering = false;

    public MazeController(Context context, int skill, String builder)
    {
        super() ;
        this.gen = (GeneratingActivity) context;
        setSkillLevel(skill);
        setBuilder(builder) ;
        panel = new MazePanel(context) ;
        mazeConfig = new MazeContainer();
        //factory = new MazeFactory() ;
        filename = null;
    }

    public MazeController(Context context)
    {
        super() ;
        //setBuilder(builder) ;
        this.context = context;
        panel = new MazePanel(context) ;
        progressBar = new ProgressBar(context);
    }

    /**
     * Register a view
     */
    public void addView(Viewer view) {
        views.add(view) ;
    }

    private void cleanViews() {
        // go through views and remove viewers as needed
        Iterator<Viewer> it = views.iterator() ;
        while (it.hasNext())
        {
            Viewer v = it.next() ;
            if ((v instanceof FirstPersonDrawer)||(v instanceof MapDrawer))
            {
                it.remove() ;
            }
        }

    }

    public int getPx() {
        return px;
    }

    public int getPy() {
        return py;
    }

//    public void init() {
//        // special case: load maze from file
//        if (null != filename) {
//            setState(StateGUI.STATE_GENERATING);
//            rset = new RangeSet();
//            panel.initBufferImage() ;
//            addView(new MazeView(this));
//            // push results into controller, imitating maze factory delivery
//            //deliver(loadMazeConfigurationFromFile(filename));
//            // reset filename, next round will be generated again
//            filename = null;
//            return;
//        }


//    /**
//     * Loads maze from file and returns a corresponding maze configuration.
//     * @param filename
//     */
//    private MazeConfiguration loadMazeConfigurationFromFile(String filename) {
//        // load maze from file
//        MazeFileReader mfr = new MazeFileReader(filename) ;
//        // obtain MazeConfiguration
//        return mfr.getMazeConfiguration();
//    }

    public MazeConfiguration getMazeConfiguration() {
        return mazeConfig ;
    }
    protected StateGUI getState(){
        return state;
    }

    public void notifyViewerRedraw() {
        // go through views and notify each one
        //panel = MazeDataHolder.getInstance().getMazePanel();
        Iterator<Viewer> it = views.iterator() ;
        rset = new RangeSet();
        while (it.hasNext())
        {
            Viewer v = it.next() ;
            v.redraw(panel, px, py, viewdx, viewdy, walkStep, Constants.VIEW_OFFSET, rset, angle) ;
        }
        // update the screen with the buffer graphics
        panel.update() ;
        play.resetMap();
    }

    /**
     * Notify all registered viewers to increment the map scale
     */
    private void notifyViewerIncrementMapScale() {
        // go through views and notify each one
        Iterator<Viewer> it = views.iterator() ;
        while (it.hasNext())
        {
            Viewer v = it.next() ;
            v.incrementMapScale() ;
        }
        // update the screen with the buffer graphics
        panel.update() ;
    }
    /**
     * Notify all registered viewers to decrement the map scale
     */
    private void notifyViewerDecrementMapScale() {
        // go through views and notify each one
        Iterator<Viewer> it = views.iterator() ;
        while (it.hasNext())
        {
            Viewer v = it.next() ;
            v.decrementMapScale() ;
        }
        // update the screen with the buffer graphics
        panel.update() ;
    }
    ////////////////////////////// get methods ///////////////////////////////////////////////////////////////
    boolean isInMapMode() {
        return mapMode ;
    }
    boolean isInShowMazeMode() {
        return showMaze ;
    }
    boolean isInShowSolutionMode() {
        return showSolution ;
    }
    public String getPercentDone(){
        return String.valueOf(percentdone) ;
    }
    public int getPercentDoneInt(){
        return percentdone;
    }
    public MazePanel getPanel() {
        return panel ;
    }
    ////////////////////////////// set methods ///////////////////////////////////////////////////////////////
    ////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
    public void setPercentDone(int percent){
        percentdone = percent;
    }
    public void setPerfect(Boolean perf) {
        perfect = perf;
    }
    protected void setShowMazeMode() {
        showSolution = true;
    }
    protected void setShowSolutionMode() {
        showSolution = true;
    }
    protected void setMapMode() {
        mapMode = true;
    }
    protected void setCurrentPosition(int x, int y)
    {
        px = x ;
        py = y ;
    }
    private void setCurrentDirection(int x, int y)
    {
        dx = x ;
        dy = y ;
    }
    protected int[] getCurrentPosition() {
        int[] result = new int[2];
        result[0] = px;
        result[1] = py;
        return result;
    }
    protected CardinalDirection getCurrentDirection() {
        return CardinalDirection.getDirection(dx, dy);
    }

    /////////////////////// Methods for debugging ////////////////////////////////
    private void dbg(String str) {
        //System.out.println(str);
    }

    private void logPosition() {
        if (!deepdebug)
            return;
        dbg("x="+viewx/Constants.MAP_UNIT+" ("+
                viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
                angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
    }

    //////////////////////// Methods for move and rotate operations ///////////////
    final double radify(int x) {
        return x*Math.PI/180;
    }
    /**
     * Helper method for walk()
     * @param dir
     * @return true if there is no wall in this direction
     */
    protected boolean checkMove(int dir) {
        CardinalDirection cd = null;
        switch (dir) {
            case 1: // forward
                cd = getCurrentDirection();
                break;
            case -1: // backward
                cd = getCurrentDirection().oppositeDirection();
                break;
            default:
                throw new RuntimeException("Unexpexted direction value: " + dir);
        }
        //return mazeConfig.getMazecells().hasNoWall(px, py, cd);
        return !mazeConfig.hasWall(px, py, cd);
    }
    /**
     * Redraw and wait, used to obtain a smooth appearance for rotate and move operations
     */
    private void slowedDownRedraw() {
        notifyViewerRedraw() ;
        try {
            Thread.currentThread().sleep(25);
        } catch (Exception e) { }
    }
    /**
     * Intermediate step during rotation, updates the screen
     */
    private void rotateStep() {
        angle = (angle+1800) % 360;
        viewdx = (int) (Math.cos(radify(angle))*(1<<16));
        viewdy = (int) (Math.sin(radify(angle))*(1<<16));
        slowedDownRedraw();
    }
    /**
     * Performs a rotation with 4 intermediate views,
     * updates the screen and the internal direction
     * @param dir for current direction
     */
    /**
     * Performs a rotation with 4 intermediate views,
     * updates the screen and the internal direction
     * @param dir for current direction
     */
    synchronized public void rotate(final int dir) {
        final int originalAngle = angle;
        if(buffering){
            return;
        }
        if(dir == 1){
            Log.v("PlayActivity", "turning left");
        }else if (dir == -1){
            Log.v("PlayActivity", "turning right");
        }
        if(driver.equalsIgnoreCase("manual")){
            if(play.progressBar.getProgress() - 3 < 0){
                play.updateBattery(play.progressBar.getProgress() - 3);
                play.switchToFinishLose();
            }
            play.updateBattery(play.progressBar.getProgress() - 3);
            Runnable rotate = new Runnable() {
                int step = 0;
                @Override
                public void run() {
                    if (step < 4) {
                        angle = originalAngle + dir * (90 * (step + 1)) / 4;
                        step++;
                        rotateStep();
                        play.rotateHandler.post(this);
                    } else {
                        setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle)));
                        buffering = false;
                    }
                }
            };
            buffering = true;
            play.rotateHandler.post(rotate);
        }else {
            final int steps = 4;
            for (int i = 0; i != steps; i++) {
                // add 1/4 of 90 degrees per step
                // if dir is -1 then subtract instead of addition
                angle = originalAngle + dir * (90 * (i + 1)) / steps;
                rotateStep();
            }
            setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle)));
        }
    }
    /**
     * Moves in the given direction with 4 intermediate steps,
     * updates the screen and the internal position
     * @param dir, only possible values are 1 (forward) and -1 (backward)
     */
//    synchronized public void walk(int dir) {
//        if (!checkMove(dir))
//            return;
//        // walkStep is a parameter of the redraw method in FirstPersonDrawer
//        // it is used there for scaling steps
//        // so walkStep is implicitly used in slowedDownRedraw which triggers the redraw
//        // operation on all listed viewers
//        for (int step = 0; step != 4; step++) {
//            walkStep += dir;
//            slowedDownRedraw();
//        }
//        setCurrentPosition(px + dir*dx, py + dir*dy) ;
//        walkStep = 0;
//        logPosition();
//    }
        /**
         * Moves in the given direction with 4 intermediate steps,
         * updates the screen and the internal position
         * @param dir
         */
        synchronized public void walk(final int dir) {
            if (!checkMove(dir) || buffering){
                return;
            }
            if(dir == 1){
                Log.v("PlayActivity", "walking forward");
            }else if (dir == -1){
                Log.v("PlayActivity", "walking backward");
            }
            if(driver.equalsIgnoreCase("manual")) {
                if(play.progressBar.getProgress() - 5 < 0){
                    play.updateBattery(play.progressBar.getProgress() - 5);
                    play.switchToFinishLose();
                }
                play.updateBattery(play.progressBar.getProgress() - 5);
                play.pathLength++;
                Runnable walking = new Runnable() {
                    int step = 4;
                    @Override
                    public void run() {
                        step--;
                        if (step >= 0) {
                            walkStep += dir;
                            notifyViewerRedraw();
                            play.walkingHandler.post(this);
                        } else {
                            setCurrentPosition(px + dir * dx, py + dir * dy);
                            buffering = false;
                            if (isOutside(px, py)) {
                                play.switchToFinish();
                            }
                            walkStep = 0;
                        }
                    }
                };
                buffering = true;
                play.walkingHandler.post(walking);
            }else{
                play.pathLength++;
                for (int step = 0; step != 4; step++) {
                    walkStep += dir;
                    slowedDownRedraw();
                }
                setCurrentPosition(px + dir * dx, py + dir * dy);
                walkStep = 0;
                logPosition();
            }
        }

    /**
     * checks if the given position is outside the maze
     * @param x
     * @param y
     * @return true if position is outside, false otherwise
     */
    public boolean isOutside(int x, int y) {
        return !mazeConfig.isValidPosition(x, y) ;
    }

    public enum UserInput {ReturnToTitle, Start, Up, Down, Left, Right, Jump, ToggleLocalMap, ToggleFullMap, ToggleSolution, ZoomIn, ZoomOut };
    /**
     * Method incorporates all reactions to keyboard input in original code,
     * The simple key listener calls this method to communicate input.
     */
    public boolean keyDown(int key) {
        // possible inputs for key: unicode char value, 0-9, A-Z, Escape, 'k','j','h','l'
        // depending on the current state of the GUI, inputs have different effects
        // implemented as a little automaton that switches state and performs necessary actions

        // if screen shows title page, keys describe level of expertise
        // create a maze according to the user's selected level
        // user types wrong key, just use 0 as a possible default value

        switch (key) {
            case 'k': case '8':
                // move forward
                walk(1);
                break;
            case 'h': case '4':
                // turn left
                rotate(1);
                break;
            case 'l': case '6':
                // turn right
                rotate(-1);
                break;
            case 'j': case '2':
                // move backward
                walk(-1);
                break;
            case Constants.ESCAPE: case 65385:
                // escape to title screen
                break;
            case ('w' & 0x1f):
                // Ctrl-w makes a step forward even through a wall
                // go to position if within maze
                if (mazeConfig.isValidPosition(px + dx, py + dy)) {
                    setCurrentPosition(px + dx, py + dy) ;
                    notifyViewerRedraw() ;
                }
                break;
            case '\t': case 'm':
                // show local information: current position and visible walls
                // precondition for showMaze and showSolution to be effective
                // acts as a toggle switch
                mapMode = !mapMode;
                notifyViewerRedraw() ;
                notifyViewerRedraw() ;
                break;
            case 'z':
                // show the whole maze
                // acts as a toggle switch
                showMaze = !showMaze;
                notifyViewerRedraw() ;
                notifyViewerRedraw() ;
                break;
            case 's':
                // show the solution as a yellow line towards the exit
                // acts as a toggle switch
                showSolution = !showSolution;
                notifyViewerRedraw() ;
                notifyViewerRedraw() ;
                break;
            case '+': case '=':
                // zoom into map
                notifyViewerIncrementMapScale() ;
                notifyViewerRedraw() ; // seems useless but it is necessary to make the screen update
                break ;
            case '-':
                // zoom out of map
                notifyViewerDecrementMapScale() ;
                notifyViewerRedraw() ; // seems useless but it is necessary to make the screen update
                break ;
        } // end of internal switch statement for playing state
        //notifyViewerRedraw() ;
        return true;
    }

    ////////// set methods for fields ////////////////////////////////
    public void setSkillLevel(int skill) {
        this.skill = skill ;
    }

    public void setBuilder(String builder) {
        if (builder.equals("DLS")) {
            this.builder = Order.Builder.DFS;
        }
        else if (builder.equals("Prim")){
            this.builder = Order.Builder.Prim;
        }
        else if (builder.equals("Eller")){
            this.builder = Order.Builder.Eller;
        }
        else
            System.out.print("No builder selected!");
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    private void setPerfect(boolean perfect) {
        this.perfect = perfect ;
    }

    ///////////////// methods to implement Order interface //////////////
    @Override
    public int getSkillLevel() {
        return skill;
    }
    @Override
    public Builder getBuilder() {
        return builder ;
    }
    @Override
    public boolean isPerfect() {
        return perfect;
    }

    public void deliver(MazeConfiguration mazeConfig) {
        this.mazeConfig = mazeConfig ;

        // WARNING: DO NOT REMOVE, USED FOR GRADING PROJECT ASSIGNMENT
        if (Cells.deepdebugWall)
        {   // for debugging: dump the sequence of all deleted walls to a log file
            // This reveals how the maze was generated
            mazeConfig.getMazecells().saveLogFile(Cells.deepedebugWallFileName);
        }
        ////////

        // adjust internal state of maze model
        // visibility settings
        showMaze = false ;
        showSolution = false ;
        mapMode = false;
        // init data structure for visible walls
        seencells = new Cells(mazeConfig.getWidth()+1,mazeConfig.getHeight()+1) ;
        // obtain starting position
        int[] start = mazeConfig.getStartingPosition() ;
        setCurrentPosition(start[0],start[1]) ;
        // set current view direction and angle
        setCurrentDirection(0, 0) ; // east direction
        viewdx = dx<<16;
        viewdy = dy<<16;
        angle = 0; // angle matches with east direction, hidden consistency constraint!
        walkStep = 0; // counts incremental steps during move/rotate operation
        cleanViews();
        addView(new FirstPersonDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT, Constants.MAP_UNIT,
                Constants.STEP_SIZE, seencells, mazeConfig.getRootnode()));
        addView(new MapDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,Constants.MAP_UNIT,
                Constants.STEP_SIZE, seencells, 10, this));
        MazeDataHolder.getInstance().setMazeController(this);
        //gen.playingActivity();
    }
    /**
     * Allows external increase to percentage in generating mode.
     * Internal value is only update if it exceeds the last value and is less or equal 100
     * @param percentage gives the new percentage on a range [0,100]
     * @return true if percentage was updated, false otherwise
     */
    public void updateProgress(int percentage) {
        if (percentdone < percentage && percentage <= 100) {
            percentdone = percentage;
//        if(percentage > gen.getProgress()) {
//            gen.updateProgress(percentage);
        }
    }

    public void setPanel(MazePanel panel){
        this.panel = panel;
    }

    public void setGeneratingActivity(GeneratingActivity gen){
        this.gen = gen;
    }

    public void setPlayingActivity(PlayActivity play){
        this.play = play;
    }

    public void setRobot(BasicRobot r) {
        this.robot = r;
    }
}

