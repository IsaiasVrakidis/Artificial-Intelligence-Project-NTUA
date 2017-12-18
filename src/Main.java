import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {
    static HashMap<String,AStarNode> map = new HashMap<String,AStarNode>();

    public static void main(String[] args) {
        System.out.println("Programm is starting..");
        int searchField = 100; //Default searchFiled is 100
        if (args.length > 0) {
            try {
                searchField = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
        }
        CSVReader reader = new CSVReader(); //  Create a CSVReader to read the input files.
        FileOutputStream outfile = null;
        try {
            outfile = new FileOutputStream("TaxisRoutes.kml");// The output files name will be TaxisRoutes.kml
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedWriter kmlWriter = new BufferedWriter(new OutputStreamWriter(outfile));// Create a writer.
        ArrayList<Taxi> taxis = new ArrayList<Taxi>();
        Client leClient = new Client();


        String taxisFile = "resources/taxis.csv";
        String clientFile = "resources/client.csv";
        String nodesFile = "resources/nodes.csv";


//      ################ Reading clients data ###############
        System.out.println("\nReading client data");

        reader.CreateBufferReader(clientFile);//  Create reader for taxis file.
        reader.ReadLine(clientFile);// Read 1st line which is useless.
        reader.ReadLine(clientFile);// Read first line of data. Client has only one line of data
        String[] line = reader.getSplitedLine();// Split the string wherever there is a comma.
        leClient.setCoordinateX(Double.parseDouble(line[0]));
        leClient.setCoordinateY(Double.parseDouble(line[1]));

//      ################ Reading taxis data ################
        System.out.println("\nReading taxis data");
        reader.CreateBufferReader(taxisFile);//   Create reader for taxis file.

        reader.ReadLine(taxisFile);// Read 1st line which is useless.
        reader.ReadLine(taxisFile);// Read first line of data.
        line = reader.getSplitedLine();// Split the string wherever there is a comma.

        while (!"terminate".equals(line[0])){
            Taxi tempTaxi = new Taxi();

            tempTaxi.setCoordinateX( Double.parseDouble(line[0]));
            tempTaxi.setCoordinateY( Double.parseDouble(line[1]));
            tempTaxi.setId(Integer.parseInt(line[2]));
            taxis.add(tempTaxi);

            reader.ReadLine(taxisFile);
            line = reader.getSplitedLine();
        }

//      ################ Reading nodes data and mapping them ################
        System.out.println("\nReading nodes data");
        reader.CreateBufferReader(nodesFile);//   Create reader for nodes file.

        reader.ReadLine(nodesFile);// Read 1st line which is useless.
        reader.ReadLine(nodesFile);// Read first line of data.
        line = reader.getSplitedLine();// Split the string wherever there is a comma.
        String currKey = null;
        String prevKey = null;
        int currId = 0;
        int prevId = 0;
        AStarNode crossroadNode;
        int numberOfCrossroads=0;
        while (!"terminate".equals(line[0])){

            AStarNode tempNode = new AStarNode();

            tempNode.setCoordinateX( Double.parseDouble(line[0]));
            tempNode.setCoordinateY( Double.parseDouble(line[1]));
            currId = Integer.parseInt(line[2]);
            tempNode.setStreetId(currId);
            if (line.length >= 4)
                tempNode.setStreetName(line[3]);
            currKey = line[0]+line[1];
            if (prevKey != null && prevId == currId){ //  If there is a node in the same street that was mapped before arrange the vicinity.
                if (!prevKey.equals(currKey)) {
                    if (!map.get(prevKey).neighbors.contains(currKey))
                        map.get(prevKey).addNeighbor(currKey);
                    if (!tempNode.neighbors.contains(prevKey))
                        tempNode.addNeighbor(prevKey);
                }
            }
            crossroadNode = map.put(currKey, tempNode);

            if (crossroadNode != null) {        //  There is a crossroad at this node so we need to merge the neighborhoods.
                int numberOfNeighbors = crossroadNode.neighbors.size();
                if (numberOfNeighbors <= 2)
                    numberOfCrossroads++;
                for (String neighborNodeKey : crossroadNode.neighbors){ //Copy the neighbors o the node we are about to throw out of the map to the current node.
                    if (!tempNode.neighbors.contains(neighborNodeKey))
                        tempNode.addNeighbor(neighborNodeKey);
                    if (!map.get(neighborNodeKey).neighbors.contains(currKey))
                        map.get(neighborNodeKey).addNeighbor(currKey);
                }

            }

            reader.ReadLine(nodesFile);
            line = reader.getSplitedLine();
            prevKey = currKey;
            prevId = currId;
        }

        System.out.println("Map has " + numberOfCrossroads + " crossroads.\n");

        //######## Searching for the nodes closest to client and taxis. #########
        Path[] taxiPaths = new Path[taxis.size()];
        for (Path tempPath: taxiPaths){
            tempPath = new Path();
        }
        String clientNode; // Find the closest node to client
        clientNode = Tools.FindClosest(leClient.getCoordinateX(), leClient.getCoordinateY(), map);

        int i=0, closestTaxiIndex=0;
        double closestTaxiPathLength=Double.MAX_VALUE;
        for (Taxi currTaxi: taxis){ // For all taxis
            String currTaxiNode = Tools.FindClosest(currTaxi.getCoordinateX(), currTaxi.getCoordinateY(), map); // Find the closest node to current taxi
            System.out.println("For Taxi " + currTaxi.getId());
            taxiPaths[i] = Tools.getBestPath(currTaxiNode, clientNode, searchField, map); //Store current taxis best path to client at the array taxiPaths.
            if (taxiPaths[i] == null){
                System.out.println("Couldent find a path for taxi " + currTaxi.getId() + "\n");
                continue;
            }
            if (taxiPaths[i].getLength()<closestTaxiPathLength){ //Search for the Path with the shortest length.
                closestTaxiPathLength = taxiPaths[i].getLength();
                closestTaxiIndex=i;
            }
            i++;
        }
        System.out.println("The length of the path for the closest taxi is " + closestTaxiPathLength);

        //################## Start Editing the output KML file #####################
        try {// Write the style for the route of closest taxi
            kmlWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<kml xmlns=\"http://earth.google.com/kml/2.1\">\n" +
                    "<Document> \n" +
                    "<name>\n" +
                    "TaxiRoutes\n" +
                    "</name> \n" +
                    "<Style id=\"Closest\"> \n" +
                    "<LineStyle> \n" +
                    "<color>\n" +
                    "ff009900\n" +
                    "</color> \n" +
                    "<width>\n" +
                    "4\n" +
                    "</width> \n" +
                    "</LineStyle> \n" +
                    "</Style> \n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (i=0; i<taxis.size(); i++){// For all the other taxis generate a random color and write a style for them.
            if (i != closestTaxiIndex){
                String randomColor;
                randomColor = Tools.getRandomHexString(8);
                try {
                    kmlWriter.write("<Style id=\"" + i + "\"> \n" +
                            "<LineStyle> \n" +
                            "<color>\n" +
                            ""+ randomColor +"\n" +
                            "</color> \n" +
                            "<width>\n" +
                            "4\n" +
                            "</width> \n" +
                            "</LineStyle> \n" +
                            "</Style> \n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (i=0; i<taxis.size(); i++){//   Write the coordinates for all the taxi routes.
            if (taxiPaths[i] != null)
                Tools.KmlPathWriter(kmlWriter, taxiPaths[i], i,i == closestTaxiIndex, map); //fill the output kml file with all the info that it needs.
        }
        System.out.println("Writing the last things at the kml file.");
        try {// Write the last kml orders.
            kmlWriter.write("\t</Document> \n" + "</kml> \n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            kmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
