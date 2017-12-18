import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
    private String[] splitedLine;
    private BufferedReader br;

    public CSVReader() {
    }

    public String[] getSplitedLine() {
        return splitedLine;
    }

    public void CreateBufferReader(String csvFile){//constructor class
        try {
            this.br = new BufferedReader(new FileReader(csvFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ReadLine(String csvFile) {

        String line = "";
        String cvsSplitBy = ",";

        try {
            // use comma as separator
            if ((line = br.readLine()) == null)
                line = "terminate";
            this.splitedLine = line.split(cvsSplitBy);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}