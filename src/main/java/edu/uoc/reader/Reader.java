package edu.uoc.reader;

import org.apache.commons.cli.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

/**
 * Created by mvelez on 14/03/16.
 */
public class Reader {

    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;

    private JTextField configFileField;

    private String configFileName;
    private String odfFileName;

    //No parameters Constructor. Start GUI
    Reader() {
        mainFrame = new JFrame("UOC Reader");

        mainFrame.setSize(500, 500);
        mainFrame.setLayout(new GridLayout(3, 3));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("", JLabel.LEFT);
        configFileField = new JTextField(1);
        statusLabel.setSize(350, 100);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.add(configFileField);
        mainFrame.setVisible(true);

    }

    Reader(String[] args) {
        Options options = new Options();

        options.addOption("config", "config path", true, "set the path for the config file");
        options.addOption("doc", "doc path", true, "set the path for the document to be parsed");

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
            Iterator commandIterator = cmd.iterator();

            if (cmd.hasOption("config"))
                this.configFileName = cmd.getOptionValue("config");

            if (cmd.hasOption("doc"))
                this.odfFileName = cmd.getOptionValue("doc");
        } catch (ParseException e) {
            System.out.println("EXCEPCIÓN DE PARSEO");
        }

    }

    private void showFileChooser() {
        headerLabel.setText("Control in action: JFileChooser");

        final JFileChooser configFileDialog = new JFileChooser();
        JButton showConfigFileDialogButton = new JButton("Open File");
        showConfigFileDialogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = configFileDialog.showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = configFileDialog.getSelectedFile();
                    configFileName = file.getName();
                    statusLabel.setText("File Selected :"
                            + file.getName());
                } else {
                    statusLabel.setText("Open command cancelled by user.");
                }
            }
        });

        final JFileChooser odtFileDialog = new JFileChooser();
        JButton showOdtFileDialogButton = new JButton("Open File");
        showOdtFileDialogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = odtFileDialog.showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = odtFileDialog.getSelectedFile();
                    configFileName = file.getName();
                    statusLabel.setText("File Selected :"
                            + file.getName());
                } else {
                    statusLabel.setText("Open command cancelled by user.");
                }
            }
        });

        controlPanel.add(showConfigFileDialogButton);
        controlPanel.add(showOdtFileDialogButton);
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(args);


        String configFile = null;
        String docFile = null;

        Reader reader = null;

        if (args.length == 0) {
            reader = new Reader();
            reader.showFileChooser();
            //Thread.sleep(5000);
            //System.exit(0);
        } else {
            reader = new Reader(args);
        }

        Config setup = new Config(reader.configFileName);
        ODTParser docParser;
        docParser = new ODTParser(reader.odfFileName);
        String language = "ES";
        String text = docParser.getText();
        /*
        try {
            text = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        try {
            new espeakTTS().generateAudio(language, text);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] Connection not available when online conversion selected");
            System.exit(1);
        }


    }
}
