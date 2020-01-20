package WebScrape;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.DataNode;

import java.nio.file.Files;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.io.File;

import java.io.*;
import java.net.MalformedURLException;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;

/** @Author Owen Patterson */

public class URLreader {

    public static String dateFormatted;
    public static String saveFilePath;
    public static File Summary;
    public static ArrayList<ArrayList<ArrayList<Dictionary>>> summarylist;
    public static FileWriter WriteL;
    public static int order;

    public static void main(String[] args) throws MalformedURLException, IOException {
        gui g = new gui("Comps Web Scraper");
        g.display(true);
    }


    public static void initial(String[] data, String destination) {
            try {
                ArrayList<ArrayList<Dictionary>> as = new ArrayList<ArrayList<Dictionary>>();
                ArrayList<Dictionary> onesummarylist = new ArrayList<Dictionary>();
                Elements elements = null;
                File oile = new File (destination + "/Scrape " + data[0] + ".csv");
                try {
                    oile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                java.io.FileWriter writer = new FileWriter(oile);
                csvUtils.writeLine(writer, Arrays.asList("Neighborhood", "Name", "Type", "Address", "Zip code", "Source URL",
                        "Price per sq ft", "Price", "SQFT", "Amenities", "Website"));
                System.out.println("Looking at " + data[1]);
                if (data[1].contains("abodo.com") && 1 == 0) {
                    for (int pages = 0; pages < 1; pages++) {
                        Document doc = Jsoup.connect("/new-york-ny?lat=40.7150330&amp;lng=-73.9842724&amp;page=" + pages
                                + "&amp;" + "place_name=Lower+East+Side+New+York%2C+NY&amp;zoom_level=12").get();
                        System.out.println(doc);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                if (data[1].contains("Trulia.com") && 1 == 0) {
                    for (int pages = 0; pages < 1; pages++) {
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                if (data[1].contains("Zillow.com") && 1 == 0) {
                    for (int pages = 0; pages < 1; pages++) {
                        //Must wait to seem human
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                //work in progress, not human enough
                if (data[1].contains("apartments.com")) {
                    try {
                        for (int pages = 0; pages < 5; pages++) {
                            try {
                                Document doc = Jsoup.connect(data[1] + pages + "/").get();
                                elements = doc.getElementsByTag("script").attr("type" , "application/ld+json");
                            } catch (IllegalArgumentException x) {
                                return;
                            }
                            for (Element scripts : elements) {
                                for (DataNode dataNode : scripts.dataNodes()) {
                                    if (dataNode.getWholeData().contains("ListItem")) {
                                        JSONObject obj = new JSONObject(dataNode.getWholeData().trim());
                                        JSONArray myObj = (JSONArray) obj.get("about");
                                        String[][] Addresses = new String[myObj.length()][5];
                                        for (int i = 0; i < myObj.length(); ++i) {
                                            JSONObject x = myObj.getJSONObject(i);
                                            try {
                                                Addresses[i][0] = x.get("name").toString();
                                                Addresses[i][1] = x.getJSONObject("Address").get("streetAddress").toString();
                                                Addresses[i][2] = x.getJSONObject("Address").get("postalCode").toString();
                                                Addresses[i][3] = x.get("url").toString();
                                                Addresses[i][4] = data[1];
                                            }
                                            catch (Exception p) {
                                                System.out.println("out");
                                            }
                                        }
                                        //Work in progress
                                        System.out.println("Looking for " + data[0] + " details on page " + pages);
                                        for (int j = 0; j < myObj.length(); ++j) {
                                            onesummarylist = followUp(data[0], Addresses[j] , writer);
                                            as.add(onesummarylist);
                                        }
                                    }
                                }
                            }
                        }

                    } catch (IOException e) {
                        System.out.println("Something went wrong initially (140)");
                    }
                }
                writer.flush();
                writer.close();
                summarylist.add(as);
            } catch (IOException e) {
                System.out.println("IOException on line (149)");
            }
    }
    public static ArrayList<Dictionary> followUp (String name1, String[] arg, FileWriter writer) {
        try {
            Document doc = Jsoup.connect(arg[3]).get();
            Elements elements = doc.getElementsByTag("script").attr("type" , "application/ld+json");
            String amenities = "";
            ArrayList midlevel = new ArrayList();
            for (Element scripts : elements) {
                for (DataNode dataNode : scripts.dataNodes()) {
                    if (dataNode.getWholeData().contains("amenityFeature")) {
                        JSONObject obj = new JSONObject(dataNode.getWholeData().trim());
                        amenities = obj.getJSONArray("mainEntity").getJSONObject(
                                0).getJSONObject("containedInPlace").get("amenityFeature").toString().replaceAll(",", " ");;
                    }
                }
            }
            Elements elem = doc.getElementsByTag("table").attr("class", "availabilityTable");
            for (Element e: elem) {
                Elements trs = e.getElementsByTag("tr");
                for (Element k :trs) {
                    try {
                        String name = k.getElementsByClass("name").get(0).text().replaceAll(",", "");
                        String beds = k.getElementsByClass("beds").get(0).text().replaceAll(",", "");
                        String sqft = k.getElementsByClass("sqft").get(0).text().replaceAll(",", "");
                        String rent = k.getElementsByClass("rent").get(0).text().replaceAll(",", "");
                        String rsq = "N/A";
                        Dictionary baselevel = new Hashtable();
                        try {
                            if (!rent.contains("-") && !sqft.contains("-")) {
                                rent = rent.replaceAll("[^0-9]" , "");
                                sqft = sqft.replaceAll("[^0-9]" , "");
                                int r = Integer.parseInt(rent);
                                int sq = Integer.parseInt(sqft);
                                rsq = Integer.toString((r * 12 / sq));
                                baselevel.put("rent", r);
                                baselevel.put("sqft", sq);
                                baselevel.put("rsq", rsq);
                                baselevel.put("name", name1);
                                midlevel.add(baselevel);
                            }
                        } catch (NumberFormatException gs) {
                        }
                        try {
                            csvUtils.writeLine(writer, Arrays.asList(name1, arg[0], beds, arg[1], arg[2],
                                    arg[3], rsq, rent, sqft, amenities, arg[4]));
                        }
                        catch (IOException zz) {
                            System.out.println("Something went wrong in write");
                        }
                    }
                    catch (IndexOutOfBoundsException p) {
                    }
                }
                return midlevel;
            }
        } catch (IOException e) {
            System.out.println("Something went wrong in followup");
        } catch (IllegalArgumentException p) {
            System.out.println("Something went wrong in followup");
        }
        return null;
    }
    public static void setLoadout(File loadout) {
        try {
            File savedLoadout = new File("savedLoadout.csv");
            savedLoadout.delete();
            Files.copy(loadout.toPath(), savedLoadout.toPath());
            System.out.println(savedLoadout.toString());
            try {
                savedLoadout.createNewFile();
                System.out.println("Loadout Saved");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (java.io.IOException x) {
            System.out.println("Loadout Not Saved");
        }
    }

    public static void makeSheet(String destination, BufferedReader br) throws IOException {
        Date d = new Date();
        dateFormatted = new SimpleDateFormat(
                "dd-M-yyyy hh:mm").format(d);
        String line = br.readLine();
        String dateTime = destination + "/Scrape " + dateFormatted + " " + line;
        File csvFile = new File(dateTime);
        csvFile.mkdir();
        line = br.readLine();
        summarylist = new ArrayList<ArrayList<ArrayList<Dictionary>>>();
        Summary = new File(dateTime + "/Summary " + dateFormatted + ".csv");
        WriteL = new FileWriter(Summary);
        order = 0;
        csvUtils.writeLine(WriteL , Arrays.asList("Name" , "Average SQFT" , "SD SQFT" , "Average Price" , "SD Price" ,
                "Average PPSQ" , "SD PPSQ"));
        while (line != null) {
            try {
                String[] data = line.split(",");
                initial(data , dateTime);
                line = br.readLine();
                order += 1;
            } catch (NullPointerException N) {
            }
        }
        try {
            for (int i = 0; i < summarylist.size(); i++) {
                int sumrent = 0;
                int sumsqft = 0;
                int sumrsf = 0;
                int withdata = 1;
                String name = "";
                int sumrentdiff = 0;
                int sumsqftdiff = 0;
                int sumrsfdiff = 0;
                int avgrent;
                int avgsqft;
                int avgrsf;
                ArrayList<ArrayList<Dictionary>> ourppty = summarylist.get(i);
                for (int j = 0; j < ourppty.size(); j++) {
                    ArrayList<Dictionary> tppty = ourppty.get(j);
                    for (int g = 0; g < tppty.size(); g++) {
                        Dictionary oppppty = tppty.get(g);
                        try {
                            sumrent += Integer.valueOf(oppppty.get("rent").toString());
                            sumsqft += Integer.valueOf(oppppty.get("sqft").toString());
                            try {
                                if (!oppppty.get("rsf").toString().equals("N/A")) {
                                    sumrsf += Integer.valueOf(oppppty.get("rsf").toString());
                                }
                            } catch (NullPointerException N) {
                            }
                            name = (String) oppppty.get("name");
                            withdata += 1;
                        } catch (IllegalArgumentException q) {
                        } catch (NullPointerException N) {
                            System.out.println(N);
                        }
                    }
                }
                avgrent = sumrent / withdata;
                avgsqft = sumsqft / withdata;
                avgrsf = sumrsf / withdata;
                System.out.println(name);
                System.out.println("avg rent " + avgrent);
                System.out.println("avg sqft " + avgsqft);
            }
        } catch (IndexOutOfBoundsException I) {
        }
    }
}
