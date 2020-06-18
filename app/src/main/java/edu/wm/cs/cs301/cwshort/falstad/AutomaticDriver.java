package edu.wm.cs.cs301.cwshort.falstad;

import edu.wm.cs.cs301.cwshort.falstad.Robot.Turn;
import edu.wm.cs.cs301.cwshort.generation.CardinalDirection;
import edu.wm.cs.cs301.cwshort.generation.Distance;
import edu.wm.cs.cs301.cwshort.ui.PlayActivity;

public abstract class AutomaticDriver implements RobotDriver {
    protected Robot robot;
    protected int width;
    protected int height;
    protected Distance distance;
    PlayActivity play;

    private float initialRobotBatteryLevel;

    /**
     * Set the Robot associated with this driver. Remembers the robot's initial battery level.
     * @param robot The robot to be associated with this driver.
     */
    @Override
    public void setRobot(Robot robot) {
        this.robot = robot;
        this.initialRobotBatteryLevel = robot.getBatteryLevel();
    }

    @Override
    public void setPlayingActivity(PlayActivity play){
        this.play = play;
    }

    @Override
    public BasicRobot getRobot() {
        return (BasicRobot) this.robot;
    }

    /**
     * Set the dimensions of the maze this driver is driving in.
     * @param width The width of the maze.
     * @param height The height of the maze.
     */
    @Override
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Set the Distance object that corresponds to the maze this driver is driving in.
     * @param distance The Distances object that corresponds to the maze this driver is driving in.
     */
    @Override
    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    /**
     * An abstract method that takes the robot and drives it out of the maze exit.
     * @return whether the robot was successfully able to exit the maze
     */
    @Override
    public abstract boolean drive2Exit() throws Exception;

    /**
     * Returns the energy consumption of the robot, based on comparing its current battery level to the one recorded at the start.
     * @return the robot's energy consumption
     */
    @Override
    public float getEnergyConsumption() {
        return this.initialRobotBatteryLevel - robot.getBatteryLevel();
    }

    /**
     * Returns the odometer reading of the robot.
     * @return odometer reading of the robot
     */
    @Override
    public int getPathLength() {
        return robot.getOdometerReading();
    }

    /**
     * Given a cardinal direction, rotate the robot to face that direction.
     * @param direction The direction the robot will face upon method completion
     */
    protected void turnToDirection(CardinalDirection direction) {
        while (robot.getCurrentDirection() != direction) {
            robot.rotate(Turn.RIGHT);
        }
    }
}

