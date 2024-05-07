package server.managers;

import server.beans.Coordinate;

import java.util.Collections;
import java.util.Stack;

public class CoordinatesManager {

    private final Stack<Coordinate> perimeterCoordinates;

    private static CoordinatesManager instance;

    private CoordinatesManager() {
        perimeterCoordinates = new Stack<>();
        initializePerimeterCoordinates();
    }

    public synchronized static CoordinatesManager getInstance() {
        if (instance == null) {
            instance = new CoordinatesManager();
        }
        return instance;
    }

    public Coordinate getFreePosition() {
        return perimeterCoordinates.pop();
    }

    private void initializePerimeterCoordinates() {
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

        System.out.println(perimeterCoordinates);
        Collections.shuffle(perimeterCoordinates);
    }

}
