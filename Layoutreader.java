package WebScrape;


import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Layoutreader {
    public static void Layoutreader(String[] layout) {
        File savedlayout = new File("savedDestination.txt");
        File supposedlayout = new File(layout[0]);
        Path sl = supposedlayout.toPath();
        try {
            if (layout[0].equals("") && savedlayout.exists()) {
                System.out.println("Using saved layout");
            }
            if (supposedlayout.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(supposedlayout));

                String st;
                br.readLine();
                while ((st = br.readLine()) != null) {
                }
            } else {
                System.out.println("No saved layout");
            }
        } catch (IOException x) {
            System.out.println("Please specify a valid destination");
        }
    }
}
