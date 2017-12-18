import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;
import java.util.Map;
import java.util.Random;

public class Tools {
    static public double getDistance(double xStart, double yStart, double xEnd, double yEnd){//  Takes two node coordinates and returns the distance between them.
        return Math.sqrt((xStart - xEnd)*(xStart - xEnd) + (yStart - yEnd)*(yStart - yEnd));
    }

    static public String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }

    static public String FindClosest(double x, double y, HashMap<String, AStarNode> map){// Takes some coordinates and finds the closets node to them
        //AStarNode resultNode = null;
        double shortestDistance = Double.MAX_VALUE;
        String resultKey=null;
        for (Map.Entry<String,AStarNode> entry : map.entrySet()) {
            String key = entry.getKey();
            AStarNode node = entry.getValue();
            if (getDistance(node.getCoordinateX(), node.getCoordinateY(), x, y) <= shortestDistance){
                shortestDistance = getDistance(node.getCoordinateX(), node.getCoordinateY(), x, y);
                resultKey = key;
            }
        }
        return resultKey;
    }

    static public String getKey (AStarNode node){
        double x = node.getCoordinateX();
        double y = node.getCoordinateY();
        return String.valueOf(x)+String.valueOf(y);
    }

    static public Path getBestPath(String startNodeKey, String goalNodeKey, int field, HashMap<String, AStarNode> map){
        ArrayList<String> openList = new ArrayList<>();
        ArrayList<String> closedList = new ArrayList<>();
        AStarNode currNode = map.get(startNodeKey);
        String currKey = startNodeKey;
        AStarNode goalNode = map.get(goalNodeKey);
        currNode.setgValue(0.0);
        currNode.sethValue(getDistance(currNode.getCoordinateX(), currNode.getCoordinateY(), goalNode.getCoordinateX(), goalNode.getCoordinateY()));
        currNode.calcfValue();
        boolean finished = false;
        int counter=0;
        while (!finished){
            double currX = currNode.getCoordinateX();
            double currY = currNode.getCoordinateY();
            for (String neighborNodeKey : currNode.neighbors) { // For all the neighbors of the current node.
                //counter++;
                if (!closedList.contains(neighborNodeKey)) {// If neighbor node is not in closeList.
                    AStarNode neighborNode = map.get(neighborNodeKey);
                    double moveToNeighborCost = getDistance(neighborNode.getCoordinateX(), neighborNode.getCoordinateY(), currNode.getCoordinateX(), currNode.getCoordinateY());
                    if (!openList.contains(neighborNodeKey) || (map.get(neighborNodeKey).getgValue() > currNode.getgValue() + moveToNeighborCost)) {
                        // If neighbor node isn't in the openList or if this path is a better path to go there
                        neighborNode.setPreviousNode(currKey);
                        neighborNode.setgValue(currNode.getgValue() + moveToNeighborCost);//G value of this neighbor node will be the g value of current node plus their distance.
                        neighborNode.sethValue(getDistance( // Setting h value of the node as the distance between it and the goal node.
                                neighborNode.getCoordinateX(), neighborNode.getCoordinateY(), goalNode.getCoordinateX(), goalNode.getCoordinateY()));
                        neighborNode.calcfValue(); //Command the neighbor node to calculate and store his f value
                    }
                    if (!openList.contains(neighborNodeKey)) { // If the node just wasn't in the openList add it if it is to be add.
                        if(openList.size()<=field) {// If there is space in the openList add it
                            openList.add(neighborNodeKey);
                        }
                        else{ //if not search if there is a node with greader fValue to replace him.
                            String maxKey = null;//Find node in openList with max fValue
                            double maxfValue = Double.MIN_VALUE;
                            for (String key: openList){
                                if (maxfValue<=map.get(key).getfValue()){
                                    maxfValue=map.get(key).getfValue();
                                    maxKey=key;
                                }
                            }
                            if (neighborNode.getfValue() < maxfValue) {
                                openList.remove(maxKey);
                                openList.add(neighborNodeKey);
                            }
                        }
                    }
                }
            }

            closedList.add(currKey);// Add current node key in the closedList as we finished with this node.
            if (openList.contains(currKey)) {// Remove it from the openList.
                openList.remove(currKey);
            }
            //need to choose the next currNode the one with the smallest fValue
            String minKey = null;
            double minfValue = Double.MAX_VALUE;
            for (String key: openList){
                if (minfValue > map.get(key).getfValue()){
                    minKey = key;
                    minfValue = map.get(minKey).getfValue();
                }
            }
            currKey = minKey;
            currNode = map.get(currKey);
            if(currKey == null){
                return null;
            }
            if (currKey.equals(goalNodeKey))// If a current node is the goal node then job's done.
                finished = true;
            counter++;
        }
        System.out.println("Max size of openList " + openList.size());
        System.out.println("Steps taken for this Path: " + counter + "\n");

        String tempKey = goalNodeKey;
        Path bestPath = new Path();
        bestPath.setLength(goalNode.getgValue());
        do{
            bestPath.nodesOfPath.add(tempKey);
            tempKey = map.get(tempKey).getPreviousNode();
        }while(!tempKey.equals(startNodeKey));
        return bestPath;
    }

    static public void KmlPathWriter (BufferedWriter writer, Path pathToPrint, int index, boolean closest, HashMap<String, AStarNode> map){
        if (closest){
            try {
                writer.write("<Placemark> \n" +
                        "<name>\n" +
                        "Taxi" +
                        String.valueOf(index) +
                        "\n" +
                        "</name> \n" +
                        "<styleUrl>\n" +
                        "#Closest\n" +
                        "</styleUrl> \n" +
                        "<LineString> \n" +
                        "<altitudeMode>\n" +
                        "relative\n" +
                        "</altitudeMode> \n" +
                        "<coordinates> ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                writer.write("<Placemark> \n" +
                        "<name>\n" +
                        "Taxi" +
                        String.valueOf(index) +
                        "\n" +
                        "</name> \n" +
                        "<styleUrl>\n" +
                        "#" + String.valueOf(index) + "\n" +
                        "</styleUrl> \n" +
                        "<LineString> \n" +
                        "<altitudeMode>\n" +
                        "relative\n" +
                        "</altitudeMode> \n" +
                        "<coordinates> ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(String nodeKey: pathToPrint.nodesOfPath){
                try {
                    writer.write(String.valueOf(map.get(nodeKey).getCoordinateX()) + "," + String.valueOf(map.get(nodeKey).getCoordinateY()) + " ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        try {
            writer.write("</coordinates> \n" +
                    "</LineString> \n" +
                    "</Placemark>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
