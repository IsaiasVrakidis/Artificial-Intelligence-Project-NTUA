import java.util.ArrayList;

public class AStarNode extends Node {
    public ArrayList<String> neighbors = new ArrayList<String>();
    private String previousNode;
    private double gValue;
    private double hValue;
    private double fValue;

    public AStarNode() {
    }

    public String getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(String previousNode) {
        this.previousNode = previousNode;
    }

    public double getgValue() {
        return gValue;
    }

    public void setgValue(double gValue) {
        this.gValue = gValue;
    }

    public double gethValue() {
        return hValue;
    }

    public void sethValue(double hValue) {
        this.hValue = hValue;
    }

    public void addNeighbor(String key){
        this.neighbors.add(key);
    }

    public void calcfValue(){
        this.fValue = this.gValue + this.hValue;
    }

    public double getfValue() {
        return fValue;
    }
}
