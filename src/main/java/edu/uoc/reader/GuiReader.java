package edu.uoc.reader;


import it.sauronsoftware.jave.EncoderException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
public class GuiReader {

    private static final Logger log= Logger.getLogger( GuiReader.class.getName());

    private static String configFileName;
    private static String odfFileName;

    private static String language;
    private static String filePattern;
    private static String filePath;

    private static String onlineTTSServiceUrl;
    private static String splitMode;


    public static void doOnLineConversion (String text) throws IOException, EncoderException {
        new OnLineTTS(onlineTTSServiceUrl).generateAudio(language, text, filePath, filePattern);
    }

    public static void doOnLineConversion (String[] text) throws IOException, EncoderException {
        Integer i = 0;
        for (String subText: text) {
            i++;
            new OnLineTTS(onlineTTSServiceUrl).generateAudio(language, subText, filePath, filePattern+String.format("%03d", i));
            doOnLineConversion(text);
        }
    }

    public static void doOfflineConversion (String text) throws IOException, EncoderException, InterruptedException {
        new EspeakTTS().generateAudio(language, text, filePath, filePattern);
    }

    public static void doOfflineConversion (String[] text) throws IOException, EncoderException, InterruptedException {
        Integer i = 0;
        for (String subText: text) {
            i++;
            new EspeakTTS().generateAudio(language, subText, filePath, filePattern+String.format("%03d", i));
        }
    }

    public static String[] processText(String text) {
        String[] pages = new String [] {"Empty"};
        if (splitMode.equals("PAGE-BREAK")){
            pages = text.split("PAGE-BREAK-MARK");
        }
        else {
            String result = text.replaceAll("PAGE-BREAK-MARK","");
            pages[0] = result;
        }
        return pages;
    }

    GuiReader() {
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
                onlineTTSServiceUrl = setup.getTtsServiceUrl();
                splitMode = setup.getSplitMode();
                String[] pages = processText(text);

                log.log(Level.INFO, "Document language: " + language);
                log.log(Level.INFO, "Output folder: " + filePath);
                log.log(Level.INFO, "Output file name: " + filePattern);
                log.log(Level.INFO, "Split file mode: " + splitMode);
                log.log(Level.INFO, "Output file number: " + pages.length);
                log.log(Level.INFO, "Is online?: " + setup.getIsOnline());
                log.log(Level.INFO, "Start of the conversion process");

                if (setup.getIsOnline()) {
                    try {
                        if (pages.length == 1 )
                            doOnLineConversion(pages[0]);
                        else
                            doOnLineConversion(pages);
                    } catch (IOException e1) {
                        log.log(Level.ERROR, e1.getMessage());
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Conversion Exception", JOptionPane.ERROR_MESSAGE);
                    } catch (EncoderException e1) {
                        log.log(Level.ERROR, e1.getMessage());
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Encoder Exception", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    try {
                        if (pages.length == 1 )
                            doOfflineConversion(pages[0]);
                        else
                            doOfflineConversion(pages);
                    } catch (IOException e1) {
                        log.log(Level.ERROR, e1.getMessage());
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Conversion Exception", JOptionPane.ERROR_MESSAGE);
                    } catch (EncoderException e1) {
                        log.log(Level.ERROR, e1.getMessage());
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Encoder Exception", JOptionPane.ERROR_MESSAGE);
                    } catch (InterruptedException e1) {
                        log.log(Level.ERROR, e1.getMessage());
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Interrupted Exception", JOptionPane.ERROR_MESSAGE);
                    }
                }
                log.log(Level.INFO, "Process finished");

            }
        });
        startButton.setBounds(500, 70, 80, 25);
        panel.add(startButton);
    }
}
