package edu.wm.cs.cs301.cwshort.falstad;

import edu.wm.cs.cs301.cwshort.falstad.Robot.Turn;
import edu.wm.cs.cs301.cwshort.generation.CardinalDirection;


public class Pledge extends AutomaticDriver {

    private static final int RIGHT_TURN_INCREMENT = 1;
    private static final int LEFT_TURN_INCREMENT = -1;

    /**
     * Drives the robot out of the exit of the maze using a Pledge algorithm.
     * @return whether the robot was able to exit the maze
     */
    @Override
    public boolean drive2Exit() throws Exception {
		/* choose random direction. */
        CardinalDirection randomDirection = CardinalDirection.East.randomDirection();

        while (robot.isAtExit() == false) {
			/* go as far as you can in that random direction. */
            turnToDirection(randomDirection);
            while (robot.hasWallInDirection(robot.getCurrentDirection()) == false) {
                robot.move(1, false);
            }
            robot.rotate(Turn.RIGHT);
            int counter = RIGHT_TURN_INCREMENT;

            while (counter != 0) {
                boolean hasWallInFront = robot.hasWallInDirection(robot.getCurrentDirection());
                boolean hasWallToLeft = robot.hasWallInDirection(robot.getCurrentDirection().rotateClockwise().rotateClockwise().rotateClockwise());

                if (!hasWallInFront && !hasWallToLeft) {
                    robot.rotate(Turn.LEFT);
                    counter += LEFT_TURN_INCREMENT;
                } else if (hasWallInFront && !hasWallToLeft) {
                    robot.rotate(Turn.LEFT);
                    counter += LEFT_TURN_INCREMENT;
                } else if (hasWallInFront && hasWallToLeft) {
                    robot.rotate(Turn.RIGHT);
                    counter += RIGHT_TURN_INCREMENT;
                } /* else, if !hasWallinFront && hasWallToLeft, do nothing */

                if (robot.hasWallInDirection(robot.getCurrentDirection()) == false) {
                    robot.move(1, false);
                }

                if (robot.isAtExit()) {
                    break;
                }

                if (robot.hasStopped()) {
                    return false;
                }
            }
        }

        return robot.stepOutOfExit();
    }
}

