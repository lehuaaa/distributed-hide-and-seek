package administration.server.repositories;

import administration.server.entities.Coordinate;

import java.util.Collections;
import java.util.Stack;

public class CoordinatesRepository {

    private final Stack<Coordinate> perimeterCoordinates;

    private static CoordinatesRepository instance;

    private CoordinatesRepository() {
        perimeterCoordinates = new Stack<>();
        initializePerimeterCoordinates();
    }

    public static CoordinatesRepository getInstance() {
        if (instance == null) {
            instance = new CoordinatesRepository();
        }
        return instance;
    }

    /* Get free perimeter coordinate */
    public Coordinate getPerimeterPosition() {
        if (perimeterCoordinates.isEmpty())
            return null;
        return perimeterCoordinates.pop();
    }

    /* Initialize list with all possible perimeter coordinates */
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

        Collections.shuffle(perimeterCoordinates);
    }
}
