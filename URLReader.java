package WebScrape;

import javax.swing.*;
import java.awt.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import ucb.gui.TopLevel;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.event.*;
import java.awt.Desktop;
import java.net.URL;

import java.net.URISyntaxException;
import javax.swing.JPanel;

class gui extends TopLevel implements View {
    public JButton Start;
    public JButton View;
    public JTextField destination;
    public JButton Open;
    public JButton New;
    public JButton Files;
    public JButton Help;
    public JButton About;
    public JButton Select;
    public String savedDestination;
    public File savedDestinationl;
    public static File savedLoadout;
    public JButton Back;

    public JFrame Main;


    gui(String title) {
        super(title, true);
        savedDestinationl = new File("savedDestination.txt");
        savedLoadout = new File("savedLoadout.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(savedDestinationl));
            savedDestination = br.readLine();
            System.out.println(savedDestination + "is saved.");
        } catch (java.io.IOException x) {
            System.out.println("No saved destination, using downloads folder.");
            String home = System.getProperty("user.home");
            savedDestination = home + "/Downloads/" + savedLoadout;
        }

        if (title.equals("Comps Web Scraper")) {

            Clicklistener click = new Clicklistener();

            Main = new JFrame("Comps Web Scraper");

            Main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Main.setSize(900, 400);
            //Creating the MenuBar and adding components
            JPanel mbpanel = new JPanel();
            Files = new JButton("Files");
            New = new JButton("New");
            About = new JButton("About");
            Help = new JButton("Help");

            mbpanel.add(Files);
            mbpanel.add(New);
            mbpanel.add(Help);
            mbpanel.add(About);

            About.addActionListener(click);

            JTextArea log = new JTextArea(20 , 20);
            JScrollPane scroll = new JScrollPane(log);
            PrintStream printStream = new PrintStream(new CustomOutputStream(log));
            System.setOut(printStream);
            System.setErr(printStream);

            JPanel panel = new JPanel();
            JPanel panelN = new JPanel();
            JPanel panelS = new JPanel();
            JLabel label = new JLabel("Destination Folder Path");
            destination = new JTextField(15);
            JLabel loadout = new JLabel("Loadout Folder Path");

            Start = new JButton("Start");
            Start.addActionListener(click);
            View = new JButton("View");
            View.addActionListener(click);
            Select = new JButton("Select");
            Select.addActionListener(click);

            panelN.add(label);
            panelN.add(destination);
            panelN.add(Start);
            panelN.add(View);


            panelS.add(loadout);
            panelS.add(Select);

            panel.add(panelN);
            panel.add(panelS);


            Main.getContentPane().add(BorderLayout.CENTER, scroll);
            Main.getContentPane().add(BorderLayout.SOUTH, panel);
            Main.setVisible(true);
        }
    }

    @Override
    public void update(URLreader a) {

    }

    public class Clicklistener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == Start) {
                if (destination.getText().equals("")) {
                    System.out.println("Using saved destination");
                    try {
                        URLreader.makeSheet(savedDestination, new BufferedReader(new FileReader(savedLoadout)));
                    }
                    catch (IOException x) {
                        System.out.println(x + " 130");
                    }
                } else {
                    try {
                        URLreader.makeSheet(destination.getText(), new BufferedReader(new FileReader(savedLoadout)));
                    }
                    catch (IOException x) {
                        System.out.println(x + " 144");
                    }
                    savedDestinationl.delete();
                    createFile("savedDestination.txt" , destination.getText());
                }
            }
            if (e.getSource() == View) {
                try {
                    if (destination.getText().equals("")) {
                        System.out.println("Using saved destination");
                        Desktop.getDesktop().open(new File(savedDestination));
                    }
                    else {
                        Desktop.getDesktop().open(new File(destination.getText()));
                    }
                } catch (Exception x) {
                    System.out.println("Please specify a valid destination");
                }
            }
            if (e.getSource() == Select) {
                try {
                    System.out.println("Finding save path");
                    JFileChooser jfc = new JFileChooser();
                    if (destination.getText().equals("") && (!savedDestination.equals(null)
                            || !savedDestination.equals(""))) {
                        jfc.setCurrentDirectory(new File
                                (savedDestination));
                        jfc.showDialog(destination, "Please Select the File");
                        jfc.setVisible(true);
                        File filename = jfc.getSelectedFile();
                        System.out.println("File name " + filename.getName());
                        URLreader.setLoadout(filename);
                    } else {
                        jfc.setCurrentDirectory(new File
                                (savedDestination));
                        jfc.showDialog(destination, "Please Select the File");
                        jfc.setVisible(true);
                        File filename = jfc.getSelectedFile();
                        System.out.println("File name " + filename.getName());
                        URLreader.setLoadout(filename);
                    }
                } catch (Exception x) {
                    System.out.println("Ouch error 162");
                }
            }
            if (e.getSource() == Open) {
                try {
                    Desktop.getDesktop().open(new File(URLreader.saveFilePath));
                } catch (Exception x) {
                    System.out.println("Finding save path");
                }
            }
            if (e.getSource() == About) {
                Desktop d = Desktop.getDesktop();
                try {
                    d.browse(new URL("https://grittydatasci.com/about").toURI());
                } catch (IOException | URISyntaxException e2) {
                    e2.printStackTrace();
                }
            }
            if (e.getSource() == New) {
                try {
                    File saved = new File(destination.getText()+"/Saved Layouts/");
                    if (!saved.exists()) {
                        saved.createNewFile();
                    }
                } catch (Exception x) {
                    System.out.println("Finding save path");
                }
            }
        }
    }

    public class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
            textArea.update(textArea.getGraphics());
        }
    }

    /**
     * Creates a new file.
     */
    public static void createFile(String fileName, String fileText) {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeFile(fileName, fileText);
    }

    /**
     * Replaces all text with the given text.
     */
    public static void writeFile(String name, String text) {
        FileWriter fr = null;
        try {
            File f = new File(name);
            fr = new FileWriter(f, false);
            fr.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
