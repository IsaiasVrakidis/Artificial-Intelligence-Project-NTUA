import java.util.ArrayList;

public class Path {
    public ArrayList<String> nodesOfPath = new ArrayList<String>();
    private double length;

    public Path() {
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}
