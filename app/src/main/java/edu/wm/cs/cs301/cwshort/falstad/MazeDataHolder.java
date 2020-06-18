package edu.wm.cs.cs301.cwshort.falstad;

import edu.wm.cs.cs301.cwshort.generation.MazeConfiguration;

/**
 * Created by cshort on 12/7/17.
 */

public class MazeDataHolder {
    private MazeController mazeControl;
    private MazeConfiguration mazeConfig;
    private MazePanel mazePan;

    public MazePanel getMazePanel() {return mazePan;}
    public void setMazePanel(MazePanel mazePan) {this.mazePan = mazePan;}

    public MazeController getMazeController() {return mazeControl;}
    public void setMazeController(MazeController mazeControl) {this.mazeControl = mazeControl;}

    public MazeConfiguration getMazeConfig() {return mazeConfig;}
    public void setMazeConfig(MazeConfiguration mazeConfig) {this.mazeConfig = mazeConfig;}

    private static final MazeDataHolder holder = new MazeDataHolder();
    public static MazeDataHolder getInstance() {return holder;}
}
