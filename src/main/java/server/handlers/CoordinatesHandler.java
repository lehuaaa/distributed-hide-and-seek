package server.handlers;

import server.beans.Coordinate;

import java.util.Collections;
import java.util.Stack;

public class CoordinatesHandler {

    private final Stack<Coordinate> perimeterCoordinates;

    private static CoordinatesHandler instance;

    private CoordinatesHandler() {
        perimeterCoordinates = new Stack<>();
        initializeCoordinates();
    }

    public synchronized static CoordinatesHandler getInstance() {
        if (instance == null) {
            instance = new CoordinatesHandler();
        }
        return instance;
    }

    public Coordinate getRandomPosition() {
        return perimeterCoordinates.pop();
    }

    private void initializeCoordinates() {
        perimeterCoordinates.clear();

        /* Bottom side -1 */
        for (int i = 0; i < 9; i++) {
            perimeterCoordinates.add(new Coordinate(i, 0));
        }

        /* Right side -1 */
        for (int i = 0; i < 9; i++) {
            perimeterCoordinates.add(new Coordinate(9, i));
        }

        /* Top side -1 */
        for (int i = 9; i > 0; i--) {
            perimeterCoordinates.add(new Coordinate(i, 9));
        }

        /* Bottom side -1 */
        for (int i = 9; i > 0; i--) {
            perimeterCoordinates.add(new Coordinate(0, i));
        }

        Collections.shuffle(perimeterCoordinates);
    }
}
