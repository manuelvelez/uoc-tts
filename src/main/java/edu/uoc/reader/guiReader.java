package edu.uoc.reader;


import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfPresentationDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by mvelezm on 26/04/16.
 */
public class guiReader {

    private static String configFileName;
    private static String odfFileName;

    private static String language;
    private static String filePattern;
    private static String filePath;

    public static void doOnLineConversion (String text) throws IOException {
        new OnLineTTS().generateAudio(language, URLEncoder.encode(text, "utf-8"), filePath, filePattern);
    }

    public static void doOfflineConversion (String text) throws IOException {
        new espeakTTS().generateAudio(language, text, filePath, filePattern);
    }

    guiReader() {
        showWindow();
    }
    public void doTheConversion() {

    }
    public static void showWindow() {
        JFrame frame = new JFrame("Demo application");
        frame.setSize(600, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {

        panel.setLayout(null);

        JLabel odfFileLabel = new JLabel("Odf File:");
        odfFileLabel.setBounds(10, 10, 80, 25);
        panel.add(odfFileLabel);

        final JTextField odfTextField = new JTextField(200);
        odfTextField.setBounds(100, 10, 400, 25);
        panel.add(odfTextField);

        final JButton odfButton = new JButton("Open");
        odfButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(odfButton);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    odfTextField.setText(selectedFile.getAbsolutePath());
                    odfFileName = selectedFile.getAbsolutePath();
                }
            }
        });
        odfButton.setBounds(500, 10, 80, 25);
        panel.add(odfButton);

        JLabel configLabel = new JLabel("Config File:");
        configLabel.setBounds(10, 40, 80, 25);
        panel.add(configLabel);

        final JTextField configTextField = new JTextField(200);
        configTextField.setBounds(100, 40, 400, 25);
        panel.add(configTextField);

        final JButton configButton = new JButton("Open");
        configButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(configButton);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    configTextField.setText(selectedFile.getAbsolutePath());
                    configFileName = selectedFile.getAbsolutePath();
                }
            }
        });
        configButton.setBounds(500, 40, 80, 25);
        panel.add(configButton);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Config setup = null;
                try {
                    setup = new Config(configFileName);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, e1.getMessage(), "IO Exception", JOptionPane.ERROR_MESSAGE);
                } catch (SAXException e1) {
                    JOptionPane.showMessageDialog(null, e1.getMessage(), "SAX Exception", JOptionPane.ERROR_MESSAGE);
                } catch (ParserConfigurationException e1) {
                    JOptionPane.showMessageDialog(null, e1.getMessage(), "Parse Exception", JOptionPane.ERROR_MESSAGE);
                } catch (NullPointerException e1) {
                    JOptionPane.showMessageDialog(null, "Config file is not set, please set and retry", "Configuration file name", JOptionPane.ERROR_MESSAGE);
                }


                OdfDocument odfDocument = null;
                try {
                    odfDocument = OdfDocument.loadDocument(odfFileName);
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(null, e1.getMessage(), "Lang Exception", JOptionPane.ERROR_MESSAGE);
                }

                String text = null;
                System.out.println("THIS IS THE REAL SHIT: " + odfDocument.getClass());

                if (odfDocument instanceof OdfTextDocument) {
                    ODTParser docParser = null;
                    try {
                        docParser = new ODTParser(odfDocument);
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    }
                    text = docParser.getText();
                }
                else if (odfDocument instanceof OdfSpreadsheetDocument) {
                    ODSParser docParser = null;
                    try {
                        docParser = new ODSParser(odfDocument);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    text = docParser.getText();
                }
                else if (odfDocument instanceof OdfPresentationDocument) {
                    ODPParser docParser = null;
                    try {
                        docParser = new ODPParser(odfDocument);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    text = docParser.getText();
                }

                language = setup.getLanguage();
                filePattern = setup.getOutputAudioPattern();
                filePath = setup.getOutputAudioPath();

                System.out.println(text);


                try {
                    if (setup.getIsOnline()) {
                        doOnLineConversion(text);
                    } else {
                        doOfflineConversion(text);
                    }
                } catch (Exception exception)
                {
                    JOptionPane.showMessageDialog(null, exception.getMessage(), "Conversion Exception", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        startButton.setBounds(500, 70, 80, 25);
        panel.add(startButton);
    }
}
