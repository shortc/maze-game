package edu.wm.cs.cs301.cwshort.falstad;

import edu.wm.cs.cs301.cwshort.generation.CardinalDirection;

public class Wizard extends AutomaticDriver {
    /**
     * Drives the robot out of the exit of the maze using a Wizard algorithm.
     * @return whether the robot was able to exit the maze
     */
    @Override
    public boolean drive2Exit() throws Exception {
        int[][] distances = distance.getDists();

        int currentDistance = Integer.MAX_VALUE;
        while (robot.isAtExit() == false && robot.hasStopped() == false) {
            int x = robot.getCurrentPosition()[0];
            int y = robot.getCurrentPosition()[1];
            currentDistance = distances[x][y];

            CardinalDirection cardinalDirection = getCardinalDirectionOfNextSpace(x, y, distances, currentDistance);
            turnToDirection(cardinalDirection);
            robot.move(1, false);

            if (robot.hasStopped()) {
                return false;
            }
        }

        return robot.stepOutOfExit();
    }

    /**
     * Determines which CardinalDirection the next space on the route to the maze exit lies in.
     * @param x the robot's current x coordinate
     * @param y the robot's current y coordinate
     * @param distances the Distance object
     * @param currentDistance the robot's current distance to the exit
     * @return the CardinalDirection in which the next space on the route is
     * @throws Exception if the method cannot find an adjacent space that is the next space on the route to the maze exit
     */
    public CardinalDirection getCardinalDirectionOfNextSpace(int x, int y, int[][] distances, int currentDistance) throws Exception {
        if (isNextSpace(x + 1, y, distances, currentDistance, CardinalDirection.East)) {
            return CardinalDirection.East;
        }
        if (isNextSpace(x - 1, y, distances, currentDistance, CardinalDirection.West)) {
            return CardinalDirection.West;
        }
        if (isNextSpace(x, y + 1, distances, currentDistance, CardinalDirection.North)) {
            return CardinalDirection.North;
        }
        if (isNextSpace(x, y - 1, distances, currentDistance, CardinalDirection.South)) {
            return CardinalDirection.South;
        }
        throw new Exception();
    }

    /**
     * Detects whether a certain space, specified by x and y coordinates, is the next space on the route to the maze exit
     * @param x x-coordinate of a space
     * @param y y-coordinate of a space
     * @param distances the inner array of the Distances object
     * @param currentDistance the robot's current distance from the exit
     * @param direction the CardinalDirection of the space relative to the Robot specified by the x and y coordinates
     * @return
     */
    public boolean isNextSpace(int x, int y, int[][] distances, int currentDistance, CardinalDirection direction) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return robot.hasWallInDirection(direction) == false && currentDistance - 1 == distances[x][y];
        } else {
            return false;
        }
    }
}

