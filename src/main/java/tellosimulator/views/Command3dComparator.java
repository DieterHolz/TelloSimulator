package tellosimulator.views;

import java.util.Comparator;

public class Command3dComparator implements Comparator<Command3d> {
    public int compare(Command3d c1, Command3d c2) {
        if (c1.getPriority() < c2.getPriority())
            return 1;
        else if (c1.getPriority() > c2.getPriority())
            return -1;
        return 0;
    }
}
